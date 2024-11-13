package com.example.crashsimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btn;
    TextInputEditText name, surname, phone_number, birthdate;
    AutoCompleteTextView gender, blood_type;
    TextInputLayout l_name, l_surname, l_phone_number, l_birthdate, l_gender, l_blood_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // SharedPreferences
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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
        l_name = findViewById(R.id.nameLayout);
        l_surname = findViewById(R.id.surnameLayout);
        l_phone_number = findViewById(R.id.phoneNumberLayout);
        l_birthdate = findViewById(R.id.birthdateLayout);
        l_gender = findViewById(R.id.genderLayout);
        l_blood_type = findViewById(R.id.bloodTypeLayout);

        // Check input as the user types
        AppHelper.SetTextChangedListener(name, l_name);
        AppHelper.SetTextChangedListener(surname, l_surname);
        AppHelper.SetTextChangedListener(phone_number, l_phone_number);
        AppHelper.SetTextChangedListener(birthdate, l_birthdate);
        AppHelper.SetTextChangedListener(gender, l_gender);
        AppHelper.SetTextChangedListener(blood_type, l_blood_type);

        // Main content
        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            if (!checkInput()) {
                return;
            }

            saveChanges();
            startActivity(new Intent(getApplicationContext(), SetQuestionsActivity.class));
        });
    }

    boolean checkInput() {
        // Just one & to disable java default short-circuiting and let every function to be executed
        return AppHelper.CheckText(name, l_name) &
                AppHelper.CheckText(surname, l_surname) &
                AppHelper.CheckText(phone_number, l_phone_number) &
                AppHelper.CheckText(gender, l_gender) &
                AppHelper.CheckText(blood_type, l_blood_type) &
                AppHelper.CheckText(birthdate, l_birthdate);
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