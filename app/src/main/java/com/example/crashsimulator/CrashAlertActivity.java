package com.example.crashsimulator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.media.AudioManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.content.Context;

import androidx.core.app.ActivityCompat;

public class CrashAlertActivity extends Activity {

    private final Handler colorChangeHandler = new Handler();
    private final int[] backgrounds = {
            R.color.white,
            R.color.yellow,
            R.color.cyan
    };
    private final Handler flashHandler = new Handler();
    private boolean isFlashOn = false;
    private final Handler alarmHandler = new Handler();
    private final Handler timerHandler = new Handler();
    LinearLayout popupLayout;
    private int index = 0;
    private MediaPlayer alarm_sound;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_crash_alert);

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        popupLayout = findViewById(R.id.popup_layout);
        Window window = getWindow();
        alarm_sound = MediaPlayer.create(this, R.raw.alarm);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        Button btnImFine = findViewById(R.id.btn_im_fine);
        Button btnImNotOkay = findViewById(R.id.btn_im_not_okay);
        ImageButton call_button = findViewById(R.id.call_button);

        btnImFine.setOnClickListener(v -> {
            Intent questionIntent = new Intent(CrashAlertActivity.this, QuestionPopupActivity.class);
            questionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Mandatory to start activity from service
            startActivity(questionIntent);
            stopHandlers();
            finish();
        });

        btnImNotOkay.setOnClickListener(v -> {
            String nameValue = sharedPref.getString(getString(R.string.name_label), getString(R.string.name_value));
            String surnameValue = sharedPref.getString(getString(R.string.surname_label), getString(R.string.surname_value));
            String phoneNumberValue = sharedPref.getString(getString(R.string.phone_number_label), getString(R.string.phone_number_value));
            String genderValue = sharedPref.getString(getString(R.string.gender_label), getString(R.string.gender_value));
            String bloodTypeValue = sharedPref.getString(getString(R.string.blood_type_label), getString(R.string.blood_type_value));
            String birthdateValue = sharedPref.getString(getString(R.string.birthdate_label), getString(R.string.birthdate_value));

            AppHelper.SendEmergencyMessage(
                    this,
                    HomeActivity.client,
                    nameValue,
                    surnameValue,
                    phoneNumberValue,
                    genderValue,
                    bloodTypeValue,
                    birthdateValue
            );

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("is_accident", true);
            startActivity(intent);
            stopHandlers();
            finish();
        });

        call_button.setOnClickListener(v -> {
            stopHandlers();
            make_call();
            finish();
        });

        setMaxVolume();

        startFlashingBackground();
        startFlashLight();
        startAlarmBackground();
        startTimer();
    }

    private void make_call(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:112"));
        if (ActivityCompat.checkSelfPermission(CrashAlertActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CrashAlertActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 100);
            finish();
        } else startActivity(callIntent);
    }

    private void setMaxVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
    }

    public void stopHandlers() {
        alarm_sound.stop();
        alarmHandler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacksAndMessages(null);
        stopFlashing();
    }

    private void startFlashingBackground() {
        colorChangeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupLayout.setBackgroundResource(backgrounds[index]);
                index = (index + 1) % backgrounds.length;
                colorChangeHandler.postDelayed(this, 500); // Change colour every 500ms
            }
        }, 500);
    }

    private void startAlarmBackground() {
        alarmHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alarm_sound.start();
                colorChangeHandler.postDelayed(this, 100); // Change colour every 500ms
            }
        }, 100);
    }

    private void startFlashLight() {
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        String cameraId;
        try {
            cameraId = cameraManager.getCameraIdList()[0];
            flashHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        isFlashOn = !isFlashOn;
                        cameraManager.setTorchMode(cameraId, isFlashOn);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    flashHandler.postDelayed(this, 500);
                }
            }, 500);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void stopFlashLight() {
        flashHandler.removeCallbacksAndMessages(null);
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
            timerHandler.postDelayed(() -> {
                String nameValue = sharedPref.getString(getString(R.string.name_label), getString(R.string.name_value));
                String surnameValue = sharedPref.getString(getString(R.string.surname_label), getString(R.string.surname_value));
                String phoneNumberValue = sharedPref.getString(getString(R.string.phone_number_label), getString(R.string.phone_number_value));
                String genderValue = sharedPref.getString(getString(R.string.gender_label), getString(R.string.gender_value));
                String bloodTypeValue = sharedPref.getString(getString(R.string.blood_type_label), getString(R.string.blood_type_value));
                String birthdateValue = sharedPref.getString(getString(R.string.birthdate_label), getString(R.string.birthdate_value));

                AppHelper.SendEmergencyMessage(
                        this,
                        HomeActivity.client,
                        nameValue,
                        surnameValue,
                        phoneNumberValue,
                        genderValue,
                        bloodTypeValue,
                        birthdateValue
                );

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }, 20000);
    }

    private void stopFlashing() {
        colorChangeHandler.removeCallbacksAndMessages(null);
        stopFlashLight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFlashing();
        alarm_sound.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopFlashing();
        alarm_sound.release();
    }
}

