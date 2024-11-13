package com.example.crashsimulator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerSensor implements SensorEventListener {

    public interface CrashListener {
        void onCrashDetected();
    }

    private final SensorManager sensorManager;
    private final int sensorDelayMs;
    private final CrashListener crashListener;
    private Sensor accelerometer;
    private float[] accelerometerValues = new float[3];
    public static final String TAG = "AccelerometerSensor";

    public AccelerometerSensor(Context context, int sensDelayMs, CrashListener listener) {
        sensorDelayMs = sensDelayMs;
        crashListener = listener;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            assert accelerometer != null;
            boolean isWakeUp = accelerometer.isWakeUpSensor();
            Log.d(TAG, "Accelerometer is wakeUpSensor? " + isWakeUp);
        }
    }

    public void start() {
        if (accelerometer != null) {
            boolean done = sensorManager.registerListener(this, accelerometer, sensorDelayMs * 1000);
            if (!done) {
                throw new RuntimeException("Accelerometer sensor is not available.");
            }
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public float[] getAccelerometerValues() {
        return accelerometerValues;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerValues, 0, event.values.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
