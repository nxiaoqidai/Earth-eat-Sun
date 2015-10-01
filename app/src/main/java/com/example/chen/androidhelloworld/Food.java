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
    private final int radius = 30;
    public float x;
    public float y;
    private Bitmap bitmapOfFood;
    private WorldView worldView;
    private int screenWidth;
    private int screenHeight;

    private int myColor;

    public Food(WorldView worldView, Bitmap bitmap, int screenHeight, int screenWidth){
        this.screenHeight=screenHeight;
        this.screenWidth=screenWidth;
        this.bitmapOfFood=bitmap;
        this.worldView=worldView;
        this.update();
    }

    public void onDraw(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);


        if (worldView.onScreen) {
            paint.setColor(myColor);
            canvas.drawCircle(x, y, radius, paint);
        }
    }

    private void update(){
        Random random=new Random();
        int r=random.nextInt(256);
        int g=random.nextInt(256);
        int b=random.nextInt(256);
        myColor=Color.rgb(r,g,b);

        x=random.nextFloat()*screenHeight;
        y=random.nextFloat()*screenWidth;
    }

}
