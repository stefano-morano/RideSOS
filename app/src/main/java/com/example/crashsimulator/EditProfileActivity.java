package com.example.crashsimulator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class EditProfileActivity extends AppCompatActivity {
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

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
        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));

        // Dropdowns
        createDropdown(R.id.gender, R.array.gender_values);
        createDropdown(R.id.bloodType, R.array.blood_type_values);
    }

    void createDropdown(int autocompleteTextViewId, int valuesId) {
        // Create an ArrayAdapter that will contain all list items
        String[] myValues = getResources().getStringArray(valuesId);

        /* Assign the name array to that adapter and
        also choose a simple layout for the list items */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, myValues);

        MaterialAutoCompleteTextView tv = findViewById(autocompleteTextViewId);

        // Assign the adapter to the tv
        tv.setAdapter(arrayAdapter);
    }
}
