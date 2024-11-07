package com.example.crashsimulator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView noRideText;
    TextView rideText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch rideSwitch;
    private AccelerometerSensor accelerometerSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        noRideText = findViewById(R.id.noRideText);
        rideText = findViewById(R.id.textViewSafeRide);

        rideSwitch = findViewById(R.id.switchRide);

        accelerometerSensor = new AccelerometerSensor(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final int x = item.getItemId();

                if (x == R.id.navigation_hospital) {
                    startActivity(new Intent(getApplicationContext(), HospitalActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                if (x == R.id.navigation_profile) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                if (x == R.id.navigation_home) return true;

                return false;
            }
        });

        rideSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                noRideText.setVisibility(View.GONE);
                rideText.setVisibility(View.VISIBLE);
                accelerometerSensor.start();
                new Thread(() -> {
                    while (true) {
                        runOnUiThread(() -> {
                            startAccelerometerService();
                            rideText.setText("Have a safe ride!");
                        });
                        try {
                            Thread.sleep(500); // Update every 500 milliseconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                stopAccelerometerService();
                noRideText.setVisibility(View.VISIBLE);
                rideText.setVisibility(View.GONE);
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
        accelerometerSensor.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        startService(serviceIntent);
    }
}
