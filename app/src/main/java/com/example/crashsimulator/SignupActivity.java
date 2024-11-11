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
    TextInputLayout l_name, l_surname, l_phone_number, l_birthdate, l_gender, l_blood_type;
    AutoCompleteTextView gender, blood_type;

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

    // TODO: We need also to sanify input
    boolean checkInput() {
        String errorText = "Mandatory";
        boolean saneInput = true;

        if (!AppHelper.HasText(name)) {
            l_name.setError(errorText);
            saneInput = false;
        } else {
            l_name.setError(null);
            l_name.setErrorEnabled(false);
        }

        if (!AppHelper.HasText(surname)) {
            l_surname.setError(errorText);
            saneInput = false;
        } else {
            l_surname.setError(null);
            l_surname.setErrorEnabled(false);
        }

        if (!AppHelper.HasText(phone_number)) {
            l_phone_number.setError(errorText);
            saneInput = false;
        } else {
            l_phone_number.setError(null);
            l_phone_number.setErrorEnabled(false);
        }

        if (!AppHelper.HasText(gender)) {
            l_gender.setError(errorText);
            saneInput = false;
        } else {
            l_gender.setError(null);
            l_gender.setErrorEnabled(false);
        }

        if (!AppHelper.HasText(blood_type)) {
            l_blood_type.setError(errorText);
            saneInput = false;
        } else {
            l_blood_type.setError(null);
            l_blood_type.setErrorEnabled(false);
        }

        if (!AppHelper.HasText(birthdate)) {
            l_birthdate.setError(errorText);
            saneInput = false;
        } else {
            l_birthdate.setError(null);
            l_birthdate.setErrorEnabled(false);
        }

        return saneInput;
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