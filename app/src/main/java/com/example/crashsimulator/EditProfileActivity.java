package com.example.crashsimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btn;
    TextInputEditText name, surname, phone_number, birthdate;
    AutoCompleteTextView gender, blood_type;

    private final static String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

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
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else return x == R.id.navigation_profile;
        });

        // Dropdowns
        AppHelper.CreateDropdown(this, findViewById(R.id.gender), R.array.gender_values);
        AppHelper.CreateDropdown(this, findViewById(R.id.bloodType), R.array.blood_type_values);

        // Bind UI
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        phone_number = findViewById(R.id.phoneNumber);
        birthdate = findViewById(R.id.birthdate);
        gender = findViewById(R.id.gender);
        blood_type = findViewById(R.id.bloodType);

        // Save changes button
        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            // TODO: Sanify input
            saveChanges();
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Restore SharedPreferences
        readSharedPreferences();
    }

    void readSharedPreferences() {
        String nameValue = sharedPref.getString(getString(R.string.name_label), getString(R.string.name_value));
        String surnameValue = sharedPref.getString(getString(R.string.surname_label), getString(R.string.surname_value));
        String phoneNumberValue = sharedPref.getString(getString(R.string.phone_number_label), getString(R.string.phone_number_value));
        String genderValue = sharedPref.getString(getString(R.string.gender_label), getString(R.string.gender_value));
        String bloodTypeValue = sharedPref.getString(getString(R.string.blood_type_label), getString(R.string.blood_type_value));
        String birthdateValue = sharedPref.getString(getString(R.string.birthdate_label), getString(R.string.birthdate_value));

        name.setText(nameValue);
        surname.setText(surnameValue);
        phone_number.setText(phoneNumberValue);
        gender.setText(genderValue, false);
        blood_type.setText(bloodTypeValue, false);
        birthdate.setText(birthdateValue);
    }

    void saveChanges() {
        // Save values of everything that changed pushing it in the sharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();

        AppHelper.PutString(editor, getString(R.string.name_label), name);
        AppHelper.PutString(editor, getString(R.string.surname_label), surname);
        AppHelper.PutString(editor, getString(R.string.phone_number_label), phone_number);
        AppHelper.PutString(editor, getString(R.string.gender_label), gender);
        AppHelper.PutString(editor, getString(R.string.blood_type_label), blood_type);
        AppHelper.PutString(editor, getString(R.string.birthdate_label), birthdate);

        // Confirm
        editor.apply();
    }
}
