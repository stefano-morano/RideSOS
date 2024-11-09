package com.example.crashsimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btn;
    ImageView editProfileView;

    TextView profile_name;
    TextInputEditText phone_number, birthdate;
    AutoCompleteTextView gender, blood_type;
    private final static String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // SharedPreferences
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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

        // Bind UI
        profile_name = findViewById(R.id.profileName);
        phone_number = findViewById(R.id.phoneNumber);
        birthdate = findViewById(R.id.birthdate);
        gender = findViewById(R.id.gender);
        blood_type = findViewById(R.id.bloodType);

        // Main content
        editProfileView = findViewById(R.id.editIcon);
        editProfileView.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)));

        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), QuestionsActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        readSharedPreferences();
    }

    void readSharedPreferences() {
        String defaultValue;

        // Full name
        defaultValue = getString(R.string.name_value);
        String nameValue = sharedPref.getString(getString(R.string.name_label), defaultValue);
        defaultValue = getString(R.string.surname_value);
        String surnameValue = sharedPref.getString(getString(R.string.surname_label), defaultValue);
        profile_name.setText(String.format("%s %s", nameValue, surnameValue));

        defaultValue = getString(R.string.phone_number_value);
        String phoneNumberValue = sharedPref.getString(getString(R.string.phone_number_label), defaultValue);
        phone_number.setText(phoneNumberValue);

        defaultValue = getString(R.string.gender_value);
        String genderValue = sharedPref.getString(getString(R.string.gender_label), defaultValue);
        gender.setText(genderValue, false);

        defaultValue = getString(R.string.blood_type_value);
        String bloodTypeValue = sharedPref.getString(getString(R.string.blood_type_label), defaultValue);
        blood_type.setText(bloodTypeValue, false);

        defaultValue = getString(R.string.birthdate_value);
        String birthdateValue = sharedPref.getString(getString(R.string.birthdate_label), defaultValue);
        birthdate.setText(birthdateValue);
    }
}
