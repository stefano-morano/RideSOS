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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        noRideText = findViewById(R.id.noRideText);
        rideText = findViewById(R.id.textViewSafeRide);

        rideSwitch = findViewById(R.id.switchRide);

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
            } else {
                noRideText.setVisibility(View.VISIBLE);
                rideText.setVisibility(View.GONE);
            }
        });


    }
}
