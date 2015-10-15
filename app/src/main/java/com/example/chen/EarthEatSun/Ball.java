package com.example.chen.EarthEatSun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by chenxixiang on 15/9/24.
 */
public class Ball {

    private float ballRadius = 50;
    private float weight = 50;

    public float x;
    public float y;

    private float xSpeed;
    private float ySpeed;

    private int globalWidth;
    private int globalHeight;

    private int screenWidth;
    private int screenHeight;

    private String name;

    public Ball(int height, int width, int h, int w, String s) {
        this.globalHeight = height;
        this.globalWidth = width;
        this.screenHeight = h;
        this.screenWidth = w;
        this.name = s;

        x = globalWidth / 2;
        y = globalHeight / 2;
        setxSpeed(0);
        setySpeed(0);
    }

    public void moveBall() {
        x = x + xSpeed;
        y = y + ySpeed;
    }

    public void updatePhysics() {
        if (x >= globalWidth - screenWidth / 2)
            x = screenWidth / 2;
        if (y >= globalHeight - screenHeight / 2)
            y = screenHeight / 2;
        if (x < screenWidth / 2)
            x = globalWidth - screenWidth / 2;
        if (y < screenHeight / 2)
            y = globalHeight - screenHeight / 2;
    }


    public void updatePhysics(Ball oriball) {
        float rex = x - oriball.x;
        float rey = y - oriball.y;
        if (x >= globalWidth - screenWidth / 2 + rex)
            x = screenWidth / 2 + rex;
        if (y >= globalHeight - screenHeight / 2 + rey)
            y = screenHeight / 2 + rey;
        if (x < screenWidth / 2 + rex)
            x = globalWidth - screenWidth / 2 + rex;
        if (y < screenHeight / 2 + rey)
            y = globalHeight - screenHeight / 2 + rey;
    }


    public void controlSpeed(float xaxis, float yaxis) {
        float tspeed = 25;

        xSpeed = tspeed * xaxis / (1 + (weight - 50) / 200);
        ySpeed = tspeed * yaxis / (1 + (weight - 50) / 200);

    }


    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public void setBallRadius(float ballRadius) {
        this.ballRadius = ballRadius;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }

}
