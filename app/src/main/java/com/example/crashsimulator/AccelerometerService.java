package com.example.crashsimulator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

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

        // Create notification in the topBar
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Crash Detection activated")
                .setContentText("Have a safe ride!")
                .setSmallIcon(R.drawable.ic_profile)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build();

        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            type = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
        }

        ServiceCompat.startForeground(
                this,
                /* id = */ 1, // Cannot be 0
                /* notification = */ notification,
                /* foregroundServiceType = */ type
        );

//        startForeground(1, notification);
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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            serviceChannel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            serviceChannel.setVibrationPattern(new long[]{0});
            serviceChannel.enableVibration(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
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
