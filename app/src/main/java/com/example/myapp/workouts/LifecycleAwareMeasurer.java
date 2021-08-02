package com.example.myapp.workouts;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.myapp.MainActivity;

import javax.inject.Inject;


public class LifecycleAwareMeasurer implements DefaultLifecycleObserver {

    private SensorManager sensorManager = null;
    private boolean running = false;
    private int totalSteps = 0;
    private int previousSteps = 0;

    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(running)
            {

            }
            Log.d(MainActivity.LOG_TAG, "temp_sensor:" + event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Inject
    public LifecycleAwareMeasurer() {

    }

    public void start(Context context) {

    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }
}
