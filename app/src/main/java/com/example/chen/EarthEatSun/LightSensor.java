package com.example.chen.EarthEatSun;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by chenxixiang on 15/10/6.
 */
public class LightSensor implements SensorEventListener {

    private WorldView worldView;
    private Context context;
    private long lastUpdate;

    private SensorManager mSensorManager;
    private Sensor lightSensor;

    public LightSensor(WorldView worldView, Context context) {
        this.worldView = worldView;
        this.context = context;

        startSensor();
    }

    public void startSensor() {
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {

        float acc = event.accuracy;

        float lux = event.values[0];
        try {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;
                if (lux > 15)
                    worldView.setDarkMode(true);
                else
                    worldView.setDarkMode(false);
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

}