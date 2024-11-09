package com.example.crashsimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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
    ArrayList<String> genderList, bloodTypeList;

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
        createDropdown(R.id.gender, R.array.gender_values);
        createDropdown(R.id.bloodType, R.array.blood_type_values);

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

    void createDropdown(int autocompleteTextViewId, int valuesId) {
        // Create an ArrayAdapter that will contain all list items
        String[] myValues = getResources().getStringArray(valuesId);

        /* Assign the name array to that adapter and
        also choose a simple layout for the list items */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown, myValues);

        MaterialAutoCompleteTextView tv = findViewById(autocompleteTextViewId);

        // Assign the adapter to the tv
        tv.setAdapter(arrayAdapter);
    }

    void readSharedPreferences() {
        String defaultValue;

        defaultValue = getString(R.string.name_value);
        String nameValue = sharedPref.getString(getString(R.string.name_label), defaultValue);
        name.setText(nameValue);

        defaultValue = getString(R.string.surname_value);
        String surnameValue = sharedPref.getString(getString(R.string.surname_label), defaultValue);
        surname.setText(surnameValue);

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

    void saveChanges() {
        // Save values of everything that changed pushing it in the sharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.name_label), String.valueOf(name.getText()));
        editor.putString(getString(R.string.surname_label), String.valueOf(surname.getText()));
        editor.putString(getString(R.string.phone_number_label), String.valueOf(phone_number.getText()));
        editor.putString(getString(R.string.gender_label), String.valueOf(gender.getText()));
        editor.putString(getString(R.string.blood_type_label), String.valueOf(blood_type.getText()));
        editor.putString(getString(R.string.birthdate_label), String.valueOf(birthdate.getText()));

        // Confirm
        editor.apply();
    }
}
