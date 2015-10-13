package com.example.chen.androidhelloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by chenxixiang on 15/10/1.
 */

public class Food {
    public int getRadius() {
        return radius;
    }

    private final int radius = 30;
    public float x;
    public float y;
    private Bitmap bitmapOfFood;
    private WorldView worldView;
    private int globalWidth;
    private int globalHeight;

    private int myColor;

    public Food(WorldView worldView, Bitmap bitmap, int screenHeight, int screenWidth){
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
//            canvas.drawCircle(x+worldView.getWidth()/2-WorldView.getXposition(), y+worldView.getHeight()/2-WorldView.getYposition(), radius, paint);
            canvas.drawCircle(worldView.getWidth()/2+y-WorldView.getYposition(), worldView.getHeight()/2+x-WorldView.getXposition(), radius, paint);

        }
    }

    private void update(){
        Random random=new Random();
        int r=random.nextInt(256);
        int g=random.nextInt(256);
        int b=random.nextInt(256);
        myColor=Color.rgb(r,g,b);

//        x=worldView.getWidth()+random.nextFloat()*(globalWidth-worldView.getWidth()*2);
//        y=worldView.getHeight()+random.nextFloat()*(globalHeight-worldView.getHeight()*2);
        x=worldView.getHeight()+random.nextFloat()*(globalWidth-worldView.getHeight()*2);
        y=worldView.getWidth()+random.nextFloat()*(globalHeight-worldView.getWidth()*2);
    }

//    private void updateppp(){
//        Random random=new Random();
//        int r=random.nextInt(256);
//        int g=random.nextInt(256);
//        int b=random.nextInt(256);
//        myColor=Color.rgb(r,g,b);
//
//        x=screenWidth/2;
//        y=screenHeight;
//    }

}

