package com.example.crashsimulator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class QuestionPopupActivity extends Activity {
    private Button answer_1;
    private Button answer_2;
    private ImageButton call_button;
    private TextView question_number;
    private TextView question_text;
    private int question_counter = 1;
    private int right_answer;
    SharedPreferences sharedPref;
    boolean completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_question);
        answer_1 = findViewById(R.id.answer_1);
        answer_2 = findViewById(R.id.answer_2);
        call_button = findViewById(R.id.call_button);
        question_number = findViewById(R.id.question_title);
        question_text = findViewById(R.id.question_text);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        set_answer();

        answer_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_answer(1);
            }
        });

        answer_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_answer(2);
            }
        });
    }

    private void check_answer(int selection){

        if (selection == 1 && right_answer == 1){
            question_counter++;
            if (question_counter > 3)
                set_final();
            else set_answer();
        }

        if (selection == 2 && right_answer == 2){
            question_counter++;
            if (question_counter > 3)
                set_final();
            else set_answer();
        }

    }

    private void set_answer(){

        int index_anserw;
        right_answer = (int) (Math.random() * 2 + 1);
        String[] wrong = new String[10];
        String right = "";
        String[] a_keys = getResources().getStringArray(R.array.answer_keys);

        switch (question_counter){
            case 1:
                question_number.setText("Question 1/3");
                question_text.setText(getString(R.string.q1_question_default));
                right = sharedPref.getString(a_keys[0], getString(R.string.q1_answer_default));
                wrong = getResources().getStringArray(R.array.q1_answers);
                break;
            case 2:
                question_number.setText("Question 2/3");
                question_text.setText(getString(R.string.q2_question_default));
                right = sharedPref.getString(a_keys[1], getString(R.string.q2_answer_default));
                wrong = getResources().getStringArray(R.array.q2_answers);
                break;
            case 3:
                question_number.setText("Question 3/3");
                question_text.setText(getString(R.string.q3_question_default));
                right = sharedPref.getString(a_keys[2], getString(R.string.q3_answer_default));
                wrong = getResources().getStringArray(R.array.q3_answers);
                break;
        }

        //check if the three different answers are the different
        do {
            index_anserw = (int) (Math.random() * 10);
        } while (wrong[index_anserw].equals(right));

        //put the right answer in the right place
        switch (right_answer){
            case 1:
                answer_1.setText(right);
                answer_2.setText(wrong[index_anserw]);
                break;
            case 2:
                answer_1.setText(wrong[index_anserw]);
                answer_2.setText(right);
                break;
        }
    }

    private void set_final(){
        completed = true;
        question_number.setText("Completed!");
        question_text.setText(getString(R.string.completed_warning));
        answer_1.setBackgroundColor(getResources().getColor(R.color.black));
        answer_1.setTextColor(getResources().getColor(R.color.white));
        answer_1.setText("Open It");
        answer_2.setText("Ignore");
    }
}
