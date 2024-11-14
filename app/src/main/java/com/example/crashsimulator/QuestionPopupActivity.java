package com.example.crashsimulator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionPopupActivity extends Activity {
    private Button anserw_1;
    private Button anserw_2;
    private ImageButton call_button;
    private TextView question_number;
    private TextView question_text;
    private int question_counter = 1;
    private int right_anserw;
    SharedPreferences sharedPref;
    boolean completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_question);
        anserw_1 = findViewById(R.id.anserw_1);
        anserw_2 = findViewById(R.id.anserw_2);
        call_button = findViewById(R.id.call_button);
        question_number = findViewById(R.id.question_title);
        question_text = findViewById(R.id.question_text);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        set_answer();

        anserw_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_anserw(1);
            }
        });

        anserw_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_anserw(2);
            }
        });
    }

    private void check_anserw(int selection){
        if (selection == 1 && right_anserw == 1){
            question_counter++;
            if (question_counter > 3)
                set_final();
            else set_answer();
        }

        if (selection == 2 && right_anserw == 2){
            question_counter++;
            if (question_counter > 3)
                set_final();
            else set_answer();
        }
    }

    private void set_answer(){

        int index_anserw;
        right_anserw = (int) (Math.random() * 2 + 1);
        String[] wrong = new String[10];
        String right = "";
        String[] a_keys = getResources().getStringArray(R.array.answer_keys);

        switch (question_counter){
            case 1:
                question_number.setText("Question 1/3");
                question_text.setText(getString(R.string.q1_question));
                right = sharedPref.getString(a_keys[0], getString(R.string.q1_answer));
                wrong = getResources().getStringArray(R.array.food_type);
                break;
            case 2:
                question_number.setText("Question 2/3");
                question_text.setText(getString(R.string.q2_question));
                right = sharedPref.getString(a_keys[1], getString(R.string.q2_answer));
                wrong = getResources().getStringArray(R.array.animal_name);
                break;
            case 3:
                question_number.setText("Question 3/3");
                question_text.setText(getString(R.string.q3_question));
                right = sharedPref.getString(a_keys[2], getString(R.string.q3_answer));
                wrong = getResources().getStringArray(R.array.woman_names);
                break;
        }

        //check if the three different answers are the different
        do {
            index_anserw = (int) (Math.random() * 10);
        } while (wrong[index_anserw].equals(right));

        //put the right answer in the right place
        switch (right_anserw){
            case 1:
                anserw_1.setText(right);
                anserw_2.setText(wrong[index_anserw]);
                break;
            case 2:
                anserw_1.setText(wrong[index_anserw]);
                anserw_2.setText(right);
                break;
        }
    }

    private void set_final(){
        completed = true;
        question_number.setText("Completed!");
        question_text.setText(getString(R.string.completed_warning));
        anserw_1.setText("Open It");
        anserw_2.setText("Ignore");
    }
}
