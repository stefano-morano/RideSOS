package com.example.crashsimulator;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

public class AccelerometerService extends Service implements AccelerometerSensor.CrashListener {
    private static final String TAG = "AccelerometerService";
    private static final String CHANNEL_ID = "AccelerometerServiceChannel";
    private static final String CHANNEL_NAME = "Crash Detection Service";
    private AccelerometerSensor accelerometerSensor;
    private Thread readingSensorThread;
    public static final int SENSOR_DELAY_MS = 500;
    private float lastMagnitude = 0;

    // Assuming CRASH_THRESHOLD is the threshold for a crash and BRAKING_THRESHOLD for a significant deceleration
    private static final float CRASH_THRESHOLD = 40.0f; // Example threshold for a crash
    private static final float BRAKING_THRESHOLD = 10.0f; // Example threshold for braking detection


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        // Inizializza AccelerometerSensor con il listener di crash
        accelerometerSensor = new AccelerometerSensor(this, 500, this);
        accelerometerSensor.start();
        readAccelerometerData();
        AppHelper.CreateNotificationChannel(this, CHANNEL_ID, CHANNEL_NAME);
        Notification notification = AppHelper.CreateNotification(this, CHANNEL_ID, "Crash Detection activated", "Have a safe ride!", R.drawable.motorbike);

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onCrashDetected() {
        Log.d(TAG, "Crash detected, notifying user");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "CrashSimulator::CrashAlertWakeLock"
        );

        wakeLock.acquire(10000);
        accelerometerSensor.stop();
        readingSensorThread.interrupt();
        Intent crashIntent = new Intent(this, CrashAlertActivity.class);
        crashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(crashIntent);
        Intent intent = new Intent("com.example.DETECT_CRASH");
        sendBroadcast(intent);
    }

    private void readAccelerometerData() {
        readingSensorThread = new Thread(() -> {
            boolean isInterrupted = false;
            Log.d(TAG, "Reading accelerometer data");

            while (!isInterrupted) {
                try {
                    float[] values = accelerometerSensor.getAccelerometerValues();
                    String data = String.format("X: %.2f, Y: %.2f, Z: %.2f", values[0], values[1], values[2]);
                    Log.d(TAG, data);
                    
                    if (detectCrash(values, lastMagnitude)) {
                        Log.d(TAG, "Crash detected!");
                        onCrashDetected();
                    }

                    lastMagnitude = calculateMagnitude(values);

                    Thread.sleep(SENSOR_DELAY_MS);
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
        });
        // Launch the thread
        readingSensorThread.start();
    }

    private float calculateMagnitude(float[] values) {
        return (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
    }

    private boolean detectCrash(float[] values, float lastMagnitude) {

        float currentMagnitude = calculateMagnitude(values);

        if (lastMagnitude > CRASH_THRESHOLD && currentMagnitude < BRAKING_THRESHOLD) {
            return true; // Sudden deceleration detected
        }

        return false;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service stopped");
        accelerometerSensor.stop();
        readingSensorThread.interrupt(); // Interrupt the sensor reading thread
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
