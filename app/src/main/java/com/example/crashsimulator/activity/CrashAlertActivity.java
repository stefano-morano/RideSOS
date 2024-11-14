package com.example.crashsimulator.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.crashsimulator.R;

public class CrashAlertActivity extends Activity {

        private Handler colorChangeHandler = new Handler();
        private final int[] backgrounds = {
            R.drawable.popup_background_white,
            R.drawable.popup_background_yellow,
            R.drawable.popup_background_cyan
        };
        private Handler flashHandler = new Handler();
        private boolean isFlashOn = false;
        LinearLayout popupLayout;
        private int index = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_crash_alert);
            popupLayout = findViewById(R.id.popup_layout);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            Button btnImFine = findViewById(R.id.btn_im_fine);
            Button btnImNotOkay = findViewById(R.id.btn_im_not_okay);

            btnImFine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(CrashAlertActivity.this, QuestionPopupActivity.class);
                    questionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necessario per avviare l'activity dal Service
                    startActivity(questionIntent);
                    finish();
                }
            });

            btnImNotOkay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Azione se l'utente non sta bene (es. chiamata a un servizio di emergenza)
                    Toast.makeText(CrashAlertActivity.this, "Calling for help!", Toast.LENGTH_SHORT).show();
                    // Qui puoi aggiungere la logica per chiamare un numero di emergenza
                    finish();
                }
            });
            //startFlashingBackground();
            startFlashLight();
        }

        private void startFlashingBackground() {
            colorChangeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    popupLayout.setBackgroundResource(backgrounds[index]);
                    index = (index + 1) % backgrounds.length;
                    colorChangeHandler.postDelayed(this, 500); // Cambia colore ogni 500ms
                }
            }, 500);
        }

        private void startFlashLight() {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId;
            try {
                cameraId = cameraManager.getCameraIdList()[0]; // Prendi la prima fotocamera
                flashHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isFlashOn = !isFlashOn;
                            cameraManager.setTorchMode(cameraId, isFlashOn); // Accendi/spegni il flash
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                        flashHandler.postDelayed(this, 500); // Lampeggia ogni 500ms
                    }
                }, 500);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        private void stopFlashLight() {
            flashHandler.removeCallbacksAndMessages(null); // Ferma il lampeggio del flash
            try {
                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false); // Spegni il flash quando il popup si chiude
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        private void stopFlashing() {
            colorChangeHandler.removeCallbacksAndMessages(null);
            stopFlashLight();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            stopFlashing();
        }
    }

