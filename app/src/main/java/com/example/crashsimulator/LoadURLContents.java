package com.example.crashsimulator;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadURLContents implements Runnable {

    Handler creator; // handler to the main activity, who creates this task
    private final String expectedContent_type;
    private final String string_URL;

    public LoadURLContents(Handler handler, String cnt_type, String strURL) {
        creator = handler;
        expectedContent_type = cnt_type;
        string_URL = strURL;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        // initial preparation of the message to communicate with the UI Thread:
        Message msg = creator.obtainMessage();
        Bundle msg_data = msg.getData();
        String response = ""; // This string will contain the loaded contents of a text resource
        StringBuilder textBuilder = new StringBuilder(); // textBuilder to store the contents of a text resource line by line
        HttpURLConnection urlConnection;
        Log.d("LoadURLContents", "Loading URL contents from " + string_URL);
        try {
            URL url = new URL(string_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            String actualContentType = urlConnection.getContentType(); // content-type header from HTTP server
            InputStream is = urlConnection.getInputStream();

            // Extract MIME type and subtype (get rid of the possible parameters present in the content-type header
            // Content-type: type/subtype;parameter1=value1;parameter2=value2...
            if((actualContentType != null) && (actualContentType.contains(";"))) {
                int beginparam = actualContentType.indexOf(";", 0);
                actualContentType = actualContentType.substring(0, beginparam);
            }

            if (expectedContent_type.equals(actualContentType)) {
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(reader);
                    // We read the text contents line by line and add them to a StringBuilder:
                    String line = in.readLine();
                    while (line != null) {
                        textBuilder.append(line).append("\n");
                        line = in.readLine();
                    }
                    response = textBuilder.toString();
            } else { // content type not supported
                response = "Actual content type different from expected ("+ actualContentType + " vs " + expectedContent_type + ")";
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            response = e.toString();
        }

        if ("".equals(response) == false) {
            msg_data.putString("text", response);
            Log.d("LoadURLContents", "Sending message to main thread");
        }
        msg.sendToTarget();
    }
}
