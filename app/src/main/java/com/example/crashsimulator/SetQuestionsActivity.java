package com.example.crashsimulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SetQuestionsActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btn;
    ImageView back;
    TextInputEditText a1, a2, a3;
    TextInputLayout l_a1, l_a2, l_a3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_questions);

        // SharedPreferences
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Back
        back = findViewById(R.id.backIcon);
        back.setOnClickListener(view -> finish());

        // Bind UI
        a1 = findViewById(R.id.question1);
        a2 = findViewById(R.id.question2);
        a3 = findViewById(R.id.question3);

        l_a1 = findViewById(R.id.question1Layout);
        l_a2 = findViewById(R.id.question2Layout);
        l_a3 = findViewById(R.id.question3Layout);

        // Check input as the user types
        AppHelper.SetTextChangedListener(a1, l_a1);
        AppHelper.SetTextChangedListener(a2, l_a2);
        AppHelper.SetTextChangedListener(a3, l_a3);

        // Save changes button
        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            if (!checkInput()) {
                return;
            }

            saveChanges();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    boolean checkInput() {
        return AppHelper.CheckText(a1, l_a1) &
                AppHelper.CheckText(a2, l_a2) &
                AppHelper.CheckText(a3, l_a3);
    }

    void saveChanges() {
        // Save values of everything that changed pushing it in the sharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();

        String[] q_keys = getResources().getStringArray(R.array.question_keys);
        String[] a_keys = getResources().getStringArray(R.array.answer_keys);

        AppHelper.PutStringHint(editor, q_keys[0], a1);
        AppHelper.PutStringHint(editor, q_keys[1], a2);
        AppHelper.PutStringHint(editor, q_keys[2], a3);
        AppHelper.PutString(editor, a_keys[0], a1);
        AppHelper.PutString(editor, a_keys[1], a2);
        AppHelper.PutString(editor, a_keys[2], a3);

        // Confirm
        editor.apply();
    }
}