package com.example.crashsimulator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerSensor implements SensorEventListener{

    private final SensorManager sensorManager;
    private final int sensorDelayMs;
    private Sensor accelerometer;
    private long timestamp;
    private float[] accelerometerValues = new float[3]; // Array per i valori [X, Y, Z]
    public static final String TAG = "AccelerometerSensor";

    // Class constructor
    public AccelerometerSensor(Context context, int sensDelayMs) {
        sensorDelayMs = sensDelayMs;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            assert accelerometer != null;
            boolean isWakeUp= accelerometer.isWakeUpSensor();
            Log.d(TAG, "Accelerometer is wakeUpSensor? " + isWakeUp);
        }
    }

    // Method for starting registering values
    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, sensorDelayMs*1000);
            // TODO: Check return value of registerListener to know if listener was actually registered or not. If true, the sensor is available and the listening has started. If false, the sensor may not be available (for whatever reason).
        }
    }

    // Method for stopping registering values
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    // Getter for accelerometer values
    public float[] getAccelerometerValues() {
        return accelerometerValues;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        timestamp = event.timestamp;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Update accelerometer values
            System.arraycopy(event.values, 0, accelerometerValues, 0, event.values.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing to implement here
    }
}