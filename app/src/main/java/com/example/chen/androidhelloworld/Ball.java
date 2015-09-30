package com.example.chen.androidhelloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.IOException;

/**
 * Created by chenxixiang on 15/9/24.
 */
public class Ball {
    private float ballRadius = 40;

    private WorldView worldView;
    private Bitmap bmp;
    public float x;
    public float y;
    private float xSpeed;
    private float ySpeed;
    private int screenWidth;
    private int screenHeight;

    public Ball(WorldView worldView, Bitmap bitmap, int screenHeight, int screenWidth) {
        this.bmp = bitmap;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.worldView = worldView;

        setX(120);
        setY(120);
        setxSpeed(0);
        setySpeed(0);
        updatePosition(x,y);
    }

    public void resetCoordinate(float screenWidth, float screenHeight, float x, float y, float xSpeed, float ySpeed){
        this.x=(x/screenWidth)*this.screenWidth;
        this.y=ballRadius;
        this.xSpeed=xSpeed;
        this.ySpeed=ySpeed*(-1);
    }

    public void moveBall(){
        x=x+xSpeed;
        y=y+ySpeed;
    }

    public void updatePhysics(){
        if(x>screenWidth-ballRadius)
            xSpeed=xSpeed/(-2);
        if(y>screenHeight-ballRadius)
            ySpeed=ySpeed/(-2);
        if(x<ballRadius)
            xSpeed=xSpeed/(-2);
        if(y<ballRadius)
            if(worldView.connected=false)
                ySpeed=ySpeed/(-2);
            else
                if(worldView.onScreen=true)
                    sendBluetoothMessage();


    }

    public void onDraw(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.YELLOW);

        updatePhysics();
        if(worldView.onScreen){
            moveBall();
            updatePosition(x,y);
            canvas.drawCircle(x,y,ballRadius,paint);
        }
    }

    public void sendBluetoothMessage(){
        try{
            StringBuffer sb = new StringBuffer();
            sb.append("ShowOnScreen");
            sb.append(",");
            sb.append(String.valueOf(screenWidth));
            sb.append(",");
            sb.append(String.valueOf(screenHeight));
            sb.append(",");
            sb.append(String.valueOf(x));
            sb.append(",");
            sb.append(String.valueOf(y));
            sb.append(",");
            sb.append(String.valueOf(xSpeed));
            sb.append(",");
            sb.append(String.valueOf(ySpeed));
            sb.append("\n");

            worldView.onScreen=false;
            worldView.outputStream.write(sb.toString().getBytes());
            worldView.outputStream.flush();

        } catch (IOException e) {
//           e.printStackTrace();
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
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

    public void updatePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
