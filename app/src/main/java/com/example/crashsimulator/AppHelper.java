package com.example.crashsimulator;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.time.Instant;


public class AppHelper {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    static public boolean HasText(EditText text) {
        return hasTextEditable(text.getText());
    }

    static public boolean HasTextHint(EditText text) {
        return hasTextCharSequence(text.getHint());
    }

    static private boolean hasTextEditable(Editable s) {
        return s != null && s.length() != 0;
    }

    static private boolean hasTextCharSequence(CharSequence s) {
        return s != null && s.length() != 0;
    }


    static public void PutString(SharedPreferences.Editor editor, String entry_key, EditText text) {
        if (HasText(text)) {
            editor.putString(entry_key, String.valueOf(text.getText()));
        }
    }

    static public void PutStringString(SharedPreferences.Editor editor, String entry_key, String text) {
        if (hasTextCharSequence(text)) {
            editor.putString(entry_key, text);
        }
    }

    static public void PutStringHint(SharedPreferences.Editor editor, String entry_key, EditText text) {
        if (HasTextHint(text)) {
            editor.putString(entry_key, String.valueOf(text.getHint()));
        }
    }

    static public void CreateDropdown(Context cxt, MaterialAutoCompleteTextView autocompleteTextView, int valuesId) {
        // Create an ArrayAdapter that will contain all list items
        String[] myValues = cxt.getResources().getStringArray(valuesId);

        /* Assign the name array to that adapter and
        also choose a simple layout for the list items */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(cxt, R.layout.item_dropdown, myValues);

        // Assign the adapter to the tv
        autocompleteTextView.setAdapter(arrayAdapter);
    }

    static public boolean CheckText(EditText value, TextInputLayout layout) {
        // TODO: We need also to sanify input
        String errorText = "Mandatory";
        boolean saneInput = true;

        if (!HasText(value)) {
            layout.setError(errorText);
            saneInput = false;
        } else {
            layout.setError(null);
            layout.setErrorEnabled(false);
        }

        return saneInput;
    }

    static public void SetTextChangedListener(EditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("HELPER", "aftertextchanged: " + s);
                CheckText(editText, layout);
            }
        });
    }

    static public void CreateNotificationChannel(Context ctx, String channel_id, String channel_name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    channel_id,
                    channel_name,
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    static public Notification CreateNotification(Context ctx, String channel_id, String title, String message, int icon_id) {
        return new NotificationCompat.Builder(ctx, channel_id)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon_id)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    static public String CreateEmergencyMessage(String name, String surname, String phoneNumber,
                                         String gender, String bloodType, String birthdate) {

        // TODO: Get actual location
        float latitude = 0f;
        float longitude = 0f;

        try {
            // Create main JSON object
            JSONObject messageJson = new JSONObject();

            // Add event and timestamp
            messageJson.put("event", "fall_detected");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                messageJson.put("timestamp", Instant.now().toString()); // Get current UTC timestamp
            }

            // Create user JSON object
            JSONObject userJson = new JSONObject();
            userJson.put("name", name);
            userJson.put("surname", surname);
            userJson.put("phone_number", phoneNumber);
            userJson.put("gender", gender);
            userJson.put("birthdate", birthdate);
            userJson.put("blood_type", bloodType);

            // Add user info to the main JSON object
            messageJson.put("user", userJson);

            // Create location JSON object
            JSONObject locationJson = new JSONObject();
            locationJson.put("latitude", latitude);
            locationJson.put("longitude", longitude);
            locationJson.put("accuracy", "5m"); // TODO: Example accuracy

            // Add location info to the main JSON object
            messageJson.put("location", locationJson);

            // Add message
            messageJson.put("message", "Urgent assistance needed: user has fallen from their bike.");

            // Convert the JSONObject to a String for MQTT transmission
            return messageJson.toString();
        } catch (Exception e) {
            return String.format(
                    "Emergency: %s %s has fallen from their bike.\n" +
                    "Contact: %s\nGender: %s\nBlood Type: %s\nBirthdate: %s\n" +
                    "Location: (%.6f, %.6f)\n" +
                    "Urgent assistance needed",
                    name, surname, phoneNumber, gender, bloodType, birthdate, latitude, longitude);
        }
    }

    static public void RequestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    // TODO: Is this different than AppHelper.AppHelper.CheckLocationPermissionsGuaranteed(Context context)?
    static public boolean HasLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    static public boolean CheckLocationPermissionsGuaranteed(Context context) {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    static public void GetCurrentLocation(Context context, OnSuccessListener<Location> listener) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setMaxUpdates(1).build();
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getLocations().isEmpty()) {
                            Location location = locationResult.getLastLocation();
                            listener.onSuccess(location);
                        }
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                },
                Looper.getMainLooper()
        );
    }

    static public String Capitalize(final String words) {
        StringBuilder result = new StringBuilder();
        for (String word : words.toLowerCase().split("\\s+")) {
            result.append(word.replaceFirst(".", word.substring(0, 1).toUpperCase())).append(" ");
        }

        return result.toString();
    }
}
