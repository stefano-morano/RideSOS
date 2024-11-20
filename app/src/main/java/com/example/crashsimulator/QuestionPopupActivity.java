package com.example.crashsimulator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;


public class QuestionPopupActivity extends Activity {
    private Button answer_1;
    private Button answer_2;
    private Button answer_3;
    private TextView question_number;
    private TextView question_text;
    private int question_counter = 1;
    private int right_answer;
    SharedPreferences sharedPref;
    boolean completed = false;
    private MediaPlayer correct_sound;
    private MediaPlayer wrong_sound;
    private static final String NUMBER = "question_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_question);
        ImageButton call_button = findViewById(R.id.call_button);
        ImageButton sos_button = findViewById(R.id.sos_button);
        answer_1 = findViewById(R.id.answer_1);
        answer_2 = findViewById(R.id.answer_2);
        answer_3 = findViewById(R.id.answer_3);
        question_number = findViewById(R.id.question_title);
        question_text = findViewById(R.id.question_text);
        correct_sound = MediaPlayer.create(this, R.raw.correct);
        wrong_sound = MediaPlayer.create(this, R.raw.wrong);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        if (savedInstanceState != null) {
            question_counter = savedInstanceState.getInt(NUMBER);
        }

        set_answer();

        answer_1.setOnClickListener(v -> {
            if (completed){
                Intent intent = new Intent("com.example.SWITCH_OFF");
                sendBroadcast(intent);
                intent = new Intent(QuestionPopupActivity.this, HospitalActivity.class);
                startActivity(intent);
                finish();
            } else check_answer(1);
        });

        answer_2.setOnClickListener(v -> {
            if (completed){
                Intent intent = new Intent("com.example.SWITCH_OFF");
                sendBroadcast(intent);
                intent = new Intent(QuestionPopupActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else check_answer(2);
        });

        answer_3.setOnClickListener(v -> {
            if (completed){
                finish();
                Intent intent = new Intent("com.example.CLOSE_APP");
                sendBroadcast(intent);
            } else check_answer(3);
        });

        sos_button.setOnClickListener(v -> {
            send_mqtt();
            Intent intent = new Intent("com.example.SWITCH_OFF");
            sendBroadcast(intent);
            intent = new Intent(QuestionPopupActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        call_button.setOnClickListener(v -> {
            make_call();
            finish();
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NUMBER, question_counter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        question_counter = savedInstanceState.getInt(NUMBER);
    }

    private void send_mqtt(){
        Intent intent = new Intent("com.example.MQTT_DETECTED");
        sendBroadcast(intent);
    }

    private void make_call(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:112"));
        if (ActivityCompat.checkSelfPermission(QuestionPopupActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QuestionPopupActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }
        startActivity(callIntent);
    }

    private void check_answer(int selection){

        if (selection == 1 && right_answer == 1){
            question_counter++;
            correct_sound.start();
            if (question_counter > 3)
                set_final();
            else set_answer();
            return;
        }

        if (selection == 2 && right_answer == 2){
            question_counter++;
            correct_sound.start();
            if (question_counter > 3)
                set_final();
            else set_answer();
            return;
        }

        if (selection == 3 && right_answer == 3){
            question_counter++;
            correct_sound.start();
            if (question_counter > 3)
                set_final();
            else set_answer();
            return;
        }

        send_mqtt();
        wrong_sound.start();
        Intent intent = new Intent("com.example.SWITCH_OFF");
        sendBroadcast(intent);
        intent = new Intent(QuestionPopupActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void set_answer(){

        int index_1, index_2;
        right_answer = (int) (Math.random() * 2) + 1;
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
            index_1 = (int) (Math.random() * 10);
            index_2 = (int) (Math.random() * 10);
        } while (wrong[index_1].equals(right) || wrong[index_1].equals(wrong[index_2]) || wrong[index_2].equals(right));

        //put the right answer in the right place
        switch (right_answer){
            case 1:
                answer_1.setText(right);
                answer_2.setText(wrong[index_1]);
                answer_3.setText(wrong[index_2]);
                break;
            case 2:
                answer_1.setText(wrong[index_1]);
                answer_2.setText(right);
                answer_3.setText(wrong[index_2]);
                break;
            case 3:
                answer_1.setText(wrong[index_1]);
                answer_2.setText(wrong[index_2]);
                answer_3.setText(right);
                break;
        }
    }

    private void set_final(){
        completed = true;
        question_number.setText("Completed!");
        question_text.setText(getString(R.string.completed_warning));
        answer_1.setBackgroundResource(R.drawable.rounded_button_black);
        answer_1.setTextColor(AppCompatResources.getColorStateList(this, R.color.white));
        answer_1.setText("Hospital");
        answer_2.setText("Main");
        answer_3.setText("Close App");
    }

    @Override
    public void onStop() {
        super.onStop();
        correct_sound.release();
        wrong_sound.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        correct_sound.release();
        wrong_sound.release();
    }
}
