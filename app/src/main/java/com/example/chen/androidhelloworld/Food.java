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
//    private int screenWidth;
//    private int screenHeight;
    private int worldWidth;
    private int worldHeight;
    Paint paint=new Paint();

    private int myColor;

    public Food(WorldView worldView, Bitmap bitmap, int worldWidth, int worldHeight){
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
        this.bitmapOfFood=bitmap;
        this.worldView=worldView;
        this.update();
    }

    public void onDraw(Canvas canvas){

        paint.setAntiAlias(true);
        float publicx = worldView.getPublicX();
        float publicy = worldView.getPublicY();

        if (worldView.onScreen) {
            paint.setColor(myColor);
            canvas.drawCircle(x-publicx+worldView.getScreenWidth()/2, y-publicy+worldView.getScreenHeight()/2, radius, paint);
        }
    }

    //randomize the position and the color of a food.
    private void update(){
        Random random=new Random();
        int r=random.nextInt(256);
        int g=random.nextInt(256);
        int b=random.nextInt(256);
        myColor=Color.rgb(r,g,b);

        x=random.nextFloat()*worldWidth;
        y=random.nextFloat()*worldHeight;
    }

    public int getRadius() {
        return radius;
    }

}
