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

public class SetQuestionsActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btn;
    ImageView back;
    AutoCompleteTextView q1, q2, q3;
    TextInputEditText a1, a2, a3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_questions);

        // SharedPreferences
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Dropdowns
        AppHelper.CreateDropdown(this, findViewById(R.id.q1_question), R.array.questions);
        AppHelper.CreateDropdown(this, findViewById(R.id.q2_question), R.array.questions);
        AppHelper.CreateDropdown(this, findViewById(R.id.q3_question), R.array.questions);

        // Back
        back = findViewById(R.id.backIcon);
        back.setOnClickListener(view -> finish());

        // Bind UI
        q1 = findViewById(R.id.q1_question);
        q2 = findViewById(R.id.q2_question);
        q3 = findViewById(R.id.q3_question);
        a1 = findViewById(R.id.q1_answer);
        a2 = findViewById(R.id.q2_answer);
        a3 = findViewById(R.id.q3_answer);

        // Save changes button
        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            // TODO: Sanify input
            saveChanges();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        });
    }

    void saveChanges() {
        // Save values of everything that changed pushing it in the sharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();

        String[] q_keys = getResources().getStringArray(R.array.question_keys);
        String[] a_keys = getResources().getStringArray(R.array.answer_keys);

        AppHelper.PutString(editor, q_keys[0], q1);
        AppHelper.PutString(editor, q_keys[1], q1);
        AppHelper.PutString(editor, q_keys[2], q1);

        AppHelper.PutString(editor, a_keys[0], a1);
        AppHelper.PutString(editor, a_keys[1], a2);
        AppHelper.PutString(editor, a_keys[2], a3);

        // Confirm
        editor.apply();
    }
}