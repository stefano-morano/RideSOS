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
    private Thread readingSensorThread;
    private AccelerometerSensor accelerometerSensor;
    public static final String CHANNEL_ID = "AccelerometerServiceChannel";
    private static final String TAG = "AccelerometerService";
    public static final int SENSOR_DELAY_MS = 2000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service started");

        accelerometerSensor = new AccelerometerSensor(this, SENSOR_DELAY_MS);
        accelerometerSensor.start();
        logAccelerometerData();

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
        Log.d(TAG, "service stopped");

        accelerometerSensor.stop(); // Stop the sensor when service is destroyed
        readingSensorThread.interrupt(); // Interrupt the sensor reading thread
        stopSelf();
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
        readingSensorThread = new Thread(() -> {
            boolean isInterrupted = false;
            while (!isInterrupted) {
                try {
                    float[] values = accelerometerSensor.getAccelerometerValues();
                    String data = String.format("X: %.2f, Y: %.2f, Z: %.2f", values[0], values[1], values[2]);
                    Log.d(TAG, data);
                    Thread.sleep(SENSOR_DELAY_MS); // Log every second
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
        });

        // Launch the thread
        readingSensorThread.start();
    }
}
