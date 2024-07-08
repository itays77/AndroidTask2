package com.example.task1.Utilities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.task1.Interfaces.MoveCallback;

public class MoveDetector {
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME; // Faster updates
    private static final float TILT_THRESHOLD = 1.5f; // Adjusted threshold
    private static final float ALPHA = 0.8f; // For low-pass filter

    private float filteredX = 0f;
    private MoveCallback moveCallback;

    public MoveDetector(Context context, MoveCallback moveCallback) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.moveCallback = moveCallback;
        initEventListener();
    }

    private void initEventListener() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                filteredX = lowPassFilter(x, filteredX);
                calculateMove(filteredX);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Not used
            }
        };
    }

    private float lowPassFilter(float input, float output) {
        return output + ALPHA * (input - output);
    }

    private void calculateMove(float x) {
        if (x > TILT_THRESHOLD) {
            moveCallback.moveLeft();
        } else if (x < -TILT_THRESHOLD) {
            moveCallback.moveRight();
        }
    }

    public void start() {
        sensorManager.registerListener(sensorEventListener, sensor, SENSOR_DELAY);
    }

    public void stop() {
        sensorManager.unregisterListener(sensorEventListener);
    }
}
