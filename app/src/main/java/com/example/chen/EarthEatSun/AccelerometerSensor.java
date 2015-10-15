package com.example.chen.EarthEatSun;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by chenxixiang on 15/9/24.
 */

public class AccelerometerSensor implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private WorldView worldView;
    private Context context;
    private long lastUpdate;

    public AccelerometerSensor(WorldView worldView, Context context) {
        this.worldView = worldView;
        this.context = context;
        this.lastUpdate = System.currentTimeMillis();
        startSensor();
    }

    public void startSensor() {
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        int accuracy = event.accuracy;
        long timestamp = event.timestamp;
        float value[] = event.values;

        try {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                reviseSpeedByTouch(value[1] * worldView.getWidth(), value[0] * worldView.getHeight());
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    public void reviseSpeedByTouch(float xTouch, float yTouch) {
        float xspeed;
        float yspeed;
        float xball = worldView.myLegion.ball.x;
        float yball = worldView.myLegion.ball.y;
        xspeed = (float) ((xTouch - worldView.getWidth() / 2) / Math.sqrt(Math.pow(xTouch - worldView.getWidth() / 2, 2) + Math.pow(yTouch - worldView.getHeight() / 2, 2)));
        yspeed = (float) ((yTouch - worldView.getHeight() / 2) / Math.sqrt(Math.pow(xTouch - worldView.getWidth() / 2, 2) + Math.pow(yTouch - worldView.getHeight() / 2, 2)));
        worldView.myLegion.ball.controlSpeed(xspeed, yspeed);
        for (int i = 0; i < worldView.myLegion.subball.size(); i++) {
            xspeed = (float) ((xTouch - (worldView.getWidth() / 2 + worldView.myLegion.subball.get(i).x - xball)) / Math.sqrt(Math.pow(xTouch - (worldView.getWidth() / 2 + worldView.myLegion.subball.get(i).x - xball), 2) + Math.pow(yTouch - (worldView.getHeight() / 2 + worldView.myLegion.subball.get(i).y - yball), 2)));
            yspeed = (float) ((yTouch - (worldView.getHeight() / 2 + worldView.myLegion.subball.get(i).y - yball)) / Math.sqrt(Math.pow(xTouch - (worldView.getWidth() / 2 + worldView.myLegion.subball.get(i).x - xball), 2) + Math.pow(yTouch - (worldView.getHeight() / 2 + worldView.myLegion.subball.get(i).y - yball), 2)));
            worldView.myLegion.subball.get(i).controlSpeed(xspeed, yspeed);
        }
    }

}