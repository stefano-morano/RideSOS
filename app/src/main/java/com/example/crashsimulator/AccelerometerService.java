package com.example.crashsimulator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AccelerometerService extends Service implements AccelerometerSensor.CrashListener {
    private static final String TAG = "AccelerometerService";
    private static final String CHANNEL_ID = "AccelerometerServiceChannel";
    private AccelerometerSensor accelerometerSensor;
    private Thread readingSensorThread;
    public static final int SENSOR_DELAY_MS = 500;
    private static final float CRASH_THRESHOLD = 30.0f; // Threshold for crash detection (in m/s²)

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        // Inizializza AccelerometerSensor con il listener di crash
        accelerometerSensor = new AccelerometerSensor(this, 500, this);
        accelerometerSensor.start();
        readAccelerometerData();
        createNotificationChannel();
        startForegroundNotification("Crash Detection activated", "Have a safe ride!");
        return START_STICKY;
    }

    private void startForegroundNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_profile)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Crash Detection Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onCrashDetected() {
        Log.d(TAG, "Crash detected, notifying user");

        // Ottieni il PowerManager e accendi temporaneamente lo schermo
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "CrashSimulator::CrashAlertWakeLock"
        );
        wakeLock.acquire(10000); // Riattiva lo schermo per 3 secondi
        accelerometerSensor.stop();
        readingSensorThread.interrupt();
        Intent crashIntent = new Intent(this, CrashAlertActivity.class);
        crashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necessario per avviare l'activity dal Service
        startActivity(crashIntent);

        Intent intent = new Intent("com.example.CRASH_DETECTED");
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
                    Thread.sleep(SENSOR_DELAY_MS); // Log every second
                    if (detectCrash(values)) {
                        Log.d(TAG, "Crash detected!");
                            onCrashDetected();
                    }
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
        });
        // Launch the thread
        readingSensorThread.start();
    }

    private boolean detectCrash(float[] values) {
        // Calcola la magnitudine dell'accelerazione
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        // Confronta con la soglia di crash
        return magnitude > CRASH_THRESHOLD;
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
