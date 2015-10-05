package com.example.chen.androidhelloworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by chenxixiang on 15/10/3.
 */
public class Obstacle{


    private final int radius = 100;
    private float x;
    private float y;
    private WorldView worldView;
    private int worldWidth;
    private int worldHeight;

    Paint paint=new Paint();


    public Obstacle(WorldView worldView, int worldWidth, int worldHeight) {
        this.worldView=worldView;
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
        this.update();
    }

    public void onDraw(Canvas canvas){
        paint.setAntiAlias(true);
        float publicX = worldView.getPublicX();
        float publicY = worldView.getPublicY();

        if (worldView.onScreen) {
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(x-publicX+worldView.getScreenWidth()/2, y-publicY+worldView.getScreenHeight()/2, radius, paint);
        }
    }

    private void update() {
        Random random=new Random();
        x=random.nextFloat()*worldWidth;
        y=random.nextFloat()*worldHeight;
    }

    public int getRadius() {
        return radius;
    }
}
