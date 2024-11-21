package com.example.crashsimulator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadURLContents implements Runnable {

    Handler creator;
    HospitalDatabase hospitalDatabase;
    String url, expectedContentType;
    SharedPreferences sharedPreferences;


    public LoadURLContents(Handler creator,
                           HospitalDatabase hospitalDatabase,
                           String url,
                           String expectedContentType) {
        this.creator = creator;
        this.hospitalDatabase = hospitalDatabase;
        this.url = url;
        this.expectedContentType = expectedContentType;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        Message msg = creator.obtainMessage();
        Bundle msg_data = msg.getData();
        String response = "";
        HttpURLConnection urlConnection;
        Log.d("LoadURLContents", "Loading URL contents from " + url);
        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            String actualContentType = urlConnection.getContentType(); // content-type header from HTTP server
            InputStream is = urlConnection.getInputStream();

            if((actualContentType != null) && (actualContentType.contains(";"))) {
                int beginparam = actualContentType.indexOf(";", 0);
                actualContentType = actualContentType.substring(0, beginparam);
            }
            Log.d("LoadURLContents", "???");
            if (expectedContentType.equals(actualContentType)) {
                List<HospitalEntity> hospitalsList = new ArrayList<>();

                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(reader);
                String line = in.readLine();
                StringBuilder textBuilder = new StringBuilder();
                while (line != null) {
                    textBuilder.append(line).append("\n");
                    line = in.readLine();
                }
                response = textBuilder.toString();
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray hospitals = jsonResponse.getJSONArray("@graph");
                for (int i = 0; i < hospitals.length(); i++) {
                    JSONObject hospital = hospitals.getJSONObject(i);
                    String name = hospital.getString("title");
                    JSONObject address = hospital.getJSONObject("address");
                    // TODO: Problem with .replace("ntilde;", "ñ")
                    String street = AppHelper.Capitalize(address.getString("street-address").replace("&amp;", ""));
                    // Parse website
                    String website = hospital.getString("relation");
                    JSONObject location = hospital.getJSONObject("location");
                    String latitudeStr = location.optString("latitude", "");
                    String longitudeStr = location.optString("longitude", "");
                    boolean isLatitudeValid = isValidDMS(latitudeStr) || isValidDecimal(latitudeStr);
                    boolean isLongitudeValid = isValidDMS(longitudeStr) || isValidDecimal(longitudeStr);
                    if (!isLatitudeValid || !isLongitudeValid) {
                        Log.d("LoadURLContents", "Invalid latitude/longitude format. Latitude: " + latitudeStr + ", Longitude: " + longitudeStr);
                    } else {
                        double longitude = 0, latitude = 0;
                        if (isValidDMS(latitudeStr)) {
                            latitude = convertDMSToDecimal(latitudeStr);
                        } else if (isValidDecimal(latitudeStr)) {
                            latitude = Double.parseDouble(latitudeStr);
                        }

                        if (isValidDMS(longitudeStr)) {
                            longitude = convertDMSToDecimal(longitudeStr);
                        } else if (isValidDecimal(longitudeStr)) {
                            longitude = Double.parseDouble(longitudeStr);
                        }

                        Log.d("LoadURLContents", "Current hospital " + name + " with address " +
                                street + " - " + latitude + " " + longitude + " - " + website);
                        HospitalEntity hospitalEntity = new HospitalEntity(name, street, website, latitude, longitude);
                        hospitalsList.add(hospitalEntity);
                    }
                }

                hospitalDatabase.hospitalDAO().deleteAllHospitals();
                hospitalDatabase.hospitalDAO().insertAll(hospitalsList);
                Log.d("LoadURLContents", String.valueOf(hospitalDatabase.hospitalDAO().getAllHospitals().size()));

                // Send msg only when hospitals downloaded
                msg_data.putString("text", "url downloaded7");
                Log.d("LoadURLContents", "Sending message to main thread");
                msg.sendToTarget();
            } else {
                response = "Actual content type different from expected ("+ actualContentType + " vs " + expectedContentType + ")";
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.d("LoadURLContents", e.getMessage());
            response = e.toString();
        }
    }

    public boolean isValidDMS(String value) {
        return value.contains("°") && value.contains("'") && (value.contains("\"") || value.matches(".*\\d$"));
    }

    public boolean isValidDecimal(String value) {
        return value.matches("^-?\\d+(\\.\\d+)?$");
    }

    public double convertDMSToDecimal(String dms) {
        try {
            dms = dms.replace("°", " ").replace("'", " ").replace("\"", " ").trim();
            String[] dmsArray = dms.split(" ");

            double degrees = Double.parseDouble(dmsArray[0]);
            double minutes = dmsArray.length > 1 ? Double.parseDouble(dmsArray[1]) : 0;
            double seconds = dmsArray.length > 2 ? Double.parseDouble(dmsArray[2]) : 0;

            double decimal = degrees + (minutes / 60) + (seconds / 3600);
            return decimal;
        } catch (Exception e) {
            Log.e("LoadURLContents", "Failed to parse DMS value: " + dms, e);
            return 0.0;
        }
    }
}
