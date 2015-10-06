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

    private float xSpeed;
    private float ySpeed;
    private float totalSpeed;

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
                float xspeed=(float)(-25*value[0]/Math.sqrt(Math.pow(value[0],2)+Math.pow(value[1],2)));
                float yspeed=(float)(25*value[1]/Math.sqrt(Math.pow(value[0],2)+Math.pow(value[1],2)));
//                worldView.ball.get(0).setxSpeed(xspeed);
//                worldView.ball.get(0).setySpeed(yspeed);
//                worldView.ball.setxSpeed(worldView.ball.getxSpeed()+((-1*value[0])/15));
//                worldView.ball.setySpeed(worldView.ball.getySpeed()+(value[1]/15));
//                if (value[0]>0 && value[1]>0) {
//                    worldView.ball.setxSpeed(-20);
//                    worldView.ball.setySpeed(20);
//                }
//
//                if (value[0]<0 && value[1]>0){
//                    worldView.ball.setxSpeed(20);
//                    worldView.ball.setySpeed(20);
//                }
//
//                if (value[0]<0 && value[1]<0){
//                    worldView.ball.setxSpeed(20);
//                    worldView.ball.setySpeed(-20);
//                }
//
//                if (value[0]>0 && value[1]<0){
//                    worldView.ball.setxSpeed(-20);
//                    worldView.ball.setySpeed(-20);
//                }


            }
        } catch (Exception e){
            Log.d("Error",e.toString());
        }
    }

    private void speedCalculator(){

    }
}
