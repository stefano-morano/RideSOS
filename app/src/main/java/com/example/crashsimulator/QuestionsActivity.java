package com.example.crashsimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class QuestionsActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    BottomNavigationView bottomNavigationView;
    ImageView backIcon;
    TextInputEditText q1, q2, q3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        // SharedPreferences
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
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

        // Main content
        q1 = findViewById(R.id.question1);
        q2 = findViewById(R.id.question2);
        q3 = findViewById(R.id.question3);

        // Back icon
        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(view -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Restore SharedPreferences
        readSharedPreferences();
    }

    void readSharedPreferences() {
        String value;
        String[] q_keys = getResources().getStringArray(R.array.question_keys);
        String[] a_keys = getResources().getStringArray(R.array.answer_keys);

        value = sharedPref.getString(q_keys[0], getString(R.string.q1_question));
        q1.setHint(value);
        value = sharedPref.getString(q_keys[1], getString(R.string.q2_question));
        q2.setHint(value);
        value = sharedPref.getString(q_keys[2], getString(R.string.q3_question));
        q3.setHint(value);

        value = sharedPref.getString(a_keys[0], getString(R.string.q1_answer));
        q1.setText(value);
        value = sharedPref.getString(a_keys[1], getString(R.string.q2_answer));
        q2.setText(value);
        value = sharedPref.getString(a_keys[2], getString(R.string.q3_answer));
        q3.setText(value);
    }

}