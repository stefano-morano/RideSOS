package com.example.crashsimulator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TextView noRideTitle, noRideText, rideTitle, rideText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch rideSwitch;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
