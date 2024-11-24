package com.example.crashsimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    ImageView profileImage;

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
                finish();
                return true;
            } else if (x == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else return x == R.id.navigation_profile;
        });

        // Bind UI
        profile_name = findViewById(R.id.profileName);
        phone_number = findViewById(R.id.phoneNumber);
        birthdate = findViewById(R.id.birthdate);
        gender = findViewById(R.id.gender);
        blood_type = findViewById(R.id.bloodType);
        profileImage = findViewById(R.id.profileImage);

        // Main content
        editProfileView = findViewById(R.id.editIcon);
        editProfileView.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)));

        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), QuestionsActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get uri of default icon
        Uri path = Uri.parse("android.resource://com.example.crashsimulator/" + R.drawable.ic_profile);
        String imgPath = path.toString();

        // Save values of everything that changed pushing it in the sharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();
        // Set default if not set
        String profileImageUriDefault = sharedPref.getString(getString(R.string.profile_image_uri_default_key), "");
        if (profileImageUriDefault.isEmpty()) {
            editor.putString(getString(R.string.profile_image_uri_default_key), imgPath);
            editor.apply();
        }
        readSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        readSharedPreferences();
    }

    void readSharedPreferences() {
        String nameValue = sharedPref.getString(getString(R.string.name_label), getString(R.string.name_value));
        String surnameValue = sharedPref.getString(getString(R.string.surname_label), getString(R.string.surname_value));
        String phoneNumberValue = sharedPref.getString(getString(R.string.phone_number_label), getString(R.string.phone_number_value));
        String genderValue = sharedPref.getString(getString(R.string.gender_label), getString(R.string.gender_value));
        String bloodTypeValue = sharedPref.getString(getString(R.string.blood_type_label), getString(R.string.blood_type_value));
        String birthdateValue = sharedPref.getString(getString(R.string.birthdate_label), getString(R.string.birthdate_value));
        String profileImageUriDefault = sharedPref.getString(getString(R.string.profile_image_uri_default_key), "");
        String profileImageUri = sharedPref.getString(getString(R.string.profile_image_uri_key), profileImageUriDefault);

        profile_name.setText(String.format("%s %s", nameValue, surnameValue));
        phone_number.setText(phoneNumberValue);
        gender.setText(genderValue, false);
        blood_type.setText(bloodTypeValue, false);
        birthdate.setText(birthdateValue);

        Log.d("TEST", "Reading profile image: " + String.valueOf(profileImageUri));
        profileImage.setImageURI(Uri.parse(profileImageUri));
    }
}
