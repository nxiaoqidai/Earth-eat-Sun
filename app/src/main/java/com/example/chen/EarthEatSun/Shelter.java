package com.example.chen.EarthEatSun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;
/**
 * Created by chenxixiang on 15/10/3.
 */
public class Shelter {

    private final int radius = 180;
    public float x;
    public float y;
    private WorldView worldView;
    private int globalWidth;
    private int globalHeight;

    private int myColor;

    public Shelter(WorldView worldView, int height, int width) {
        this.globalHeight = height;
        this.globalWidth = width;

        this.worldView = worldView;
        this.update();
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(myColor);
        canvas.drawCircle(worldView.getWidth() / 2 + x - WorldView.getXposition(), worldView.getHeight() / 2 + y - WorldView.getYposition(), radius, paint);
    }

    private void update() {
        Random random = new Random();
        myColor = Color.GREEN;

        x = worldView.getWidth() + random.nextFloat() * (globalWidth - worldView.getWidth() * 2);
        y = worldView.getHeight() + random.nextFloat() * (globalHeight - worldView.getHeight() * 2);
    }

}
