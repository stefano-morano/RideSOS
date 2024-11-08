package com.example.crashsimulator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AccelerometerService extends Service {

    private AccelerometerSensor accelerometerSensor;
    public static final String CHANNEL_ID = "AccelerometerServiceChannel";
    private static final String TAG = "AccelerometerService";

    @Override
    public void onCreate() {
        super.onCreate();
        accelerometerSensor = new AccelerometerSensor(this);
        accelerometerSensor.start();
        logAccelerometerData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SOSRide")
                .setContentText("SOSRide is working")
                .setSmallIcon(R.drawable.ic_profile)
                .build();
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accelerometerSensor.stop(); // Ferma il sensore quando il servizio viene distrutto
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Accelerometer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setVibrationPattern(new long[]{0});
            serviceChannel.enableVibration(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void logAccelerometerData() {
        new Thread(() -> {
            while (true) {
                float[] values = accelerometerSensor.getAccelerometerValues();
                String data = String.format("X: %.2f, Y: %.2f, Z: %.2f", values[0], values[1], values[2]);
                // Logga i dati nella Logcat
                Log.d(TAG, data);
                try {
                    Thread.sleep(1000); // Log ogni secondo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
