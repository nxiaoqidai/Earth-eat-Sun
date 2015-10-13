package com.example.chen.androidhelloworld;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by chenxixiang on 15/9/24.
 */

public class AccelerometerSensor implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private WorldView worldView;
    private Context context;
    private long lastUpdate;

    public AccelerometerSensor(WorldView worldView,Context context){
        this.worldView=worldView;
        this.context=context;
        this.lastUpdate=System.currentTimeMillis();
        startSensor();
    }

    public void startSensor(){
        mSensorManager=(SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        mAccelerometer =mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_GAME);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void onSensorChanged(SensorEvent event){
        int accuracy=event.accuracy;
        long timestamp = event.timestamp;
        float value[] = event.values;

        try {
            long curTime = System.currentTimeMillis();
            if ((curTime-lastUpdate)>100){
                lastUpdate=curTime;

//                worldView.ball.setxSpeed(worldView.ball.getxSpeed()+((-1*value[0])/30));
//                worldView.ball.setySpeed(worldView.ball.getySpeed()+(value[1]/30));

            }
        } catch (Exception e){
            Log.d("Error",e.toString());
        }
    }
}
