package com.example.crashsimulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btn;
    ImageView profileImage;
    TextInputEditText name, surname, phone_number, birthdate;
    AutoCompleteTextView gender, blood_type;
    TextInputLayout l_name, l_surname, l_phone_number, l_birthdate, l_gender, l_blood_type;
    Uri newProfileImageUri;

    private static final int PICK_IMAGE_REQUEST_CODE = 1001;

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
                finish();
                return true;
            } else if (x == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else return x == R.id.navigation_profile;
        });

        // Dropdowns
        AppHelper.CreateDropdown(this, findViewById(R.id.gender), R.array.gender_values);
        AppHelper.CreateDropdown(this, findViewById(R.id.bloodType), R.array.blood_type_values);

        // Bind UI
        profileImage = findViewById(R.id.profileImage);
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

        // Save changes button
        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            if (!checkInput()) {
                return;
            }

            saveChanges();
            finish();
        });

        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);  // Open image picker
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                int newWidth = 300;
                int newHeight = 300;

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

                profileImage.setImageBitmap(resizedBitmap);
                newProfileImageUri = bitmapToUri(resizedBitmap, this);
                Log.d("TEST", "changed (temp) profileImageUri to: " + String.valueOf(newProfileImageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri bitmapToUri(Bitmap bitmap, Context context) {
        File outputDir = context.getCacheDir(); // Use internal storage (you can also use external storage if needed)
        File outputFile = new File(outputDir, "profile_pic.png"); // You can set your own name for the image file

        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Save bitmap as PNG
            out.flush();
            return Uri.fromFile(outputFile); // Return the URI of the saved file
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if an error occurs
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
        String profileImageUriDefault = sharedPref.getString(getString(R.string.profile_image_uri_default_key), "");
        String profileImageUri = sharedPref.getString(getString(R.string.profile_image_uri_key), profileImageUriDefault);

        name.setText(nameValue);
        surname.setText(surnameValue);
        phone_number.setText(phoneNumberValue);
        gender.setText(genderValue, false);
        blood_type.setText(bloodTypeValue, false);
        birthdate.setText(birthdateValue);
        profileImage.setImageURI(Uri.parse(profileImageUri));
    }

    boolean checkInput() {
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
        AppHelper.PutStringString(editor, getString(R.string.profile_image_uri_key), String.valueOf(newProfileImageUri));

        // Confirm
        editor.apply();
    }

}
