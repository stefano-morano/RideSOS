package com.example.crashsimulator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    MQTTClient client;
    BottomNavigationView bottomNavigationView;
    TextView noRideTitle, noRideText, rideTitle, rideText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch rideSwitch;
    private static final String TAG = "HomeActivity";

    private static final String CONTENT_TYPE_HOSPITALS_JSON = "application/json";
    private static final String HOSPITALS_URL_JSON = "https://datos.madrid.es/portal/site/egob/menuitem.ac61933d6ee3c31cae77ae7784f1a5a0/?vgnextoid=00149033f2201410VgnVCM100000171f5a0aRCRD&format=json&file=0&filename=212769-0-atencion-medica&mgmtid=da7437ac37efb410VgnVCM2000000c205a0aRCRD&preview=full";
    HospitalDatabase hospitalDatabase;

    SharedPreferences sharedPreferences;
    ExecutorService es;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        hospitalDatabase = HospitalDatabase.getInstance(this);
        es = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hospitalsDownloaded", true);
                editor.commit();
            }
        };

        // check if the hospitals list is already downloaded and stored in DB
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        //if (!sharedPreferences.getBoolean("hospitalsDownloaded", false)) {
            es.execute(new LoadURLContents(handler, hospitalDatabase, HOSPITALS_URL_JSON, CONTENT_TYPE_HOSPITALS_JSON));
        //}

        // Bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            final int x = item.getItemId();

            if (x == R.id.navigation_hospital) {
                startActivity(new Intent(getApplicationContext(), HospitalActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (x == R.id.navigation_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else return x == R.id.navigation_home;
        });


        // Create MQTT Client
        client = new MQTTClient();

        // Main content
        noRideTitle = findViewById(R.id.textViewTitleOff);
        noRideText = findViewById(R.id.textViewMessageOff);
        rideTitle = findViewById(R.id.textViewTitleActive);
        rideText = findViewById(R.id.textViewMessageActive);

        rideSwitch = findViewById(R.id.switchRide);
        rideSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "switch on");
                noRideTitle.setVisibility(View.INVISIBLE);
                noRideText.setVisibility(View.INVISIBLE);
                rideTitle.setVisibility(View.VISIBLE);
                rideText.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    runOnUiThread(this::startAccelerometerService);
                }).start();
            } else {
                stopAccelerometerService();
                noRideTitle.setVisibility(View.VISIBLE);
                noRideText.setVisibility(View.VISIBLE);
                rideTitle.setVisibility(View.INVISIBLE);
                rideText.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startAccelerometerService() {
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        startService(serviceIntent);
    }

    private void stopAccelerometerService() {
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect client to broker
        // TODO: Think where it is better to put this, when we should connect to the broker?
        client.connectToBroker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Disconnect client from the broker
        // TODO: Think where it is better to put this, when we should disconnect to the broker?
        client.disconnectFromBroker();
    }
}
