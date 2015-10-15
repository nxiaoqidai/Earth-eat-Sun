package com.example.chen.EarthEatSun;

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
    private WorldView worldView;
    private int globalWidth;
    private int globalHeight;

    private int myColor;

    public Food(WorldView worldView, int height, int width) {
        this.globalHeight = height;
        this.globalWidth = width;
        this.worldView = worldView;
        this.initializeSelf();
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(myColor);
        canvas.drawCircle(worldView.getWidth() / 2 + x - WorldView.getXposition(), worldView.getHeight() / 2 + y - WorldView.getYposition(), radius, paint);

    }

    private void initializeSelf() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        myColor = Color.rgb(r, g, b);

        x = worldView.getWidth() + random.nextFloat() * (globalWidth - worldView.getWidth() * 2);
        y = worldView.getHeight() + random.nextFloat() * (globalHeight - worldView.getHeight() * 2);
    }

}