package com.example.crashsimulator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetQuestionsActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    Button btn;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_questions);

        // Main content
        back = findViewById(R.id.backIcon);
        back.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignupActivity.class)));

        btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeActivity.class)));
    }
}