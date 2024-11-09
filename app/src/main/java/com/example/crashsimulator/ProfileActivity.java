package com.example.crashsimulator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    Button btn;
    ImageView editProfileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            final int x = item.getItemId();

            if (x == R.id.navigation_hospital) {
                startActivity(new Intent(getApplicationContext(), HospitalActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (x == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else return x == R.id.navigation_profile;
        });

        // Main content
        editProfileView = findViewById(R.id.editIcon);
        editProfileView.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)));
    }
}
