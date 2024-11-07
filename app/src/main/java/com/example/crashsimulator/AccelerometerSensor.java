package com.example.crashsimulator;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerSensor implements SensorEventListener{

    private final SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] accelerometerValues = new float[3]; // Array per i valori [X, Y, Z]

    // Costruttore della classe
    public AccelerometerSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    // Metodo per iniziare a registrare l'accelerometro
    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, 5000);
        }
    }

    // Metodo per fermare la registrazione del sensore
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    // Metodo per restituire i valori attuali dell'accelerometro
    public float[] getAccelerometerValues() {
        return accelerometerValues;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Aggiorna i valori dell'accelerometro
            System.arraycopy(event.values, 0, accelerometerValues, 0, event.values.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non è necessario implementare nulla qui per l'uso base dell'accelerometro
    }
}