package com.example.chen.androidhelloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;
/**
 * Created by chenxixiang on 15/10/3.
 */
public class Obstacle {
    public int getRadius() {
        return radius;
    }

    private final int radius = 100;
    public float x;
    public float y;
    private Bitmap bitmapOfFood;
    private WorldView worldView;
    private int globalWidth;
    private int globalHeight;

    private int myColor;

    public Obstacle(WorldView worldView, Bitmap bitmap, int screenHeight, int screenWidth){
        this.globalHeight=screenHeight;
        this.globalWidth=screenWidth;
        this.bitmapOfFood=bitmap;
        this.worldView=worldView;
        this.update();
    }

    public void onDraw(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);


        if (worldView.onScreen) {
            paint.setColor(myColor);
            canvas.drawCircle(worldView.getWidth()/2+y-WorldView.getYposition(), worldView.getHeight()/2+x-WorldView.getXposition(), radius, paint);        }
    }

    private void update(){
        Random random=new Random();
        myColor=Color.GREEN;

        x=worldView.getHeight()+random.nextFloat()*(globalWidth-worldView.getHeight()*2);
        y=worldView.getWidth()+random.nextFloat()*(globalHeight-worldView.getWidth()*2);
    }

}

