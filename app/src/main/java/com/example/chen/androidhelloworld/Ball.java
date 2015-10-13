package com.example.chen.androidhelloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.io.IOException;

/**
 * Created by chenxixiang on 15/9/24.
 */
public class Ball {

    private float ballRadius = 50;
    private int score=0;
    private float weight=50;
    private float preweight=50;


    private WorldView worldView;
    private Bitmap bmp;
    public float x;
    public float y;
    private float xSpeed;
    private float ySpeed;
    private int globalWidth;
    private int globalHeight;

    public static String playerName;
    Paint paintName=new Paint();
    float textBaseY;


    public Ball(WorldView worldView, Bitmap bitmap, int screenHeight, int screenWidth) {
        this.bmp = bitmap;
        this.globalHeight = screenHeight;
        this.globalWidth = screenWidth;
        this.worldView = worldView;

        x=globalWidth/2;
        y=globalHeight/2;
        setxSpeed(0);
        setySpeed(0);
     //   updatePosition(x, y);


        paintName.setAntiAlias(true);
        paintName.setColor(Color.BLACK);
        paintName.setTextSize(30);
        paintName.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paintName.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        textBaseY = worldView.getHeight()/2 +fontHeight/4;
    }

//    public void resetCoordinate(float screenWidth, float screenHeight, float x, float y, float xSpeed, float ySpeed){
//        this.x=(x/screenWidth)*this.screenWidth;
//        this.y=(y/screenHeight)*this.screenHeight;
//        this.xSpeed=xSpeed;
//        this.ySpeed=ySpeed*(-1);
//    }


    public void moveBall(){
        x=x+xSpeed;
        y=y+ySpeed;
    }

    public void updatePhysics(){
        if(x>=globalWidth-worldView.getHeight()/2)
            x=worldView.getHeight()/2;
        if(y>=globalHeight -worldView.getWidth()/2)
            y=worldView.getWidth()/2;
        if(x<worldView.getHeight()/2)
            x=globalWidth-worldView.getHeight()/2;
        if(y<worldView.getWidth()/2)
            y=globalHeight -worldView.getWidth()/2;


//        if(x>screenWidth-ballRadius)
//            xSpeed=0;
//        if(y>screenHeight*3-ballRadius)
//            ySpeed=0;
//        if(x<ballRadius)
//            xSpeed=0;
//        if(y<ballRadius)
//            ySpeed=0;
//            if(worldView.connected=false)
//                ySpeed=ySpeed/(-1);
//            else
//                if(worldView.onScreen=true)
//                    sendBluetoothMessage();


//        if((x>globalWidth-ballRadius)&&(xSpeed>0))
//            xSpeed=xSpeed/(-1);
//        if((y>globalHeight-ballRadius)&&(ySpeed>0))
//            ySpeed=ySpeed/(-1);
//        if((x<ballRadius)&&(xSpeed<0))
//            xSpeed=xSpeed/(-1);
//        if((y<ballRadius)&&(ySpeed<0))
//            ySpeed=ySpeed/(-1);



    }

    public void updateSpeed(){
        if(preweight!=weight) {

            float xaxis=(float)(xSpeed / Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2)));
            float yaxis=(float)(ySpeed / Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2)));
            controlSpeed(xaxis,yaxis);

            preweight=weight;
        }
    }

    public void controlSpeed(float xaxis,float yaxis){
        float tspeed=25;
//        if(xSpeed==0&&ySpeed==0)
//            tspeed=25;
//        else
//            tspeed=(float)(Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2)));

        xSpeed=tspeed*xaxis/(1 + (weight - 50) /200);
        ySpeed=tspeed*yaxis/(1 + (weight - 50) / 200);

    }

    public void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        updataScore();
        updateSpeed();
        updatePhysics();
        if(worldView.onScreen){
            moveBall();
       //     updatePosition(x, y);
            canvas.drawCircle(worldView.getWidth() / 2, worldView.getHeight() / 2, ballRadius, paint);
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(worldView.getWidth() / 2, worldView.getHeight() / 2, ballRadius - 5, paint);
            canvas.drawText(playerName, worldView.getWidth() / 2,textBaseY ,paintName);

        }
    }

    public void subOnDraw(Canvas canvas,Ball ori_ball,boolean flag){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        if(flag)
        updateSpeed();
    //    updatePhysics();
        if(worldView.onScreen){
            moveBall();
            updatePosition(ori_ball);
            canvas.drawCircle(worldView.getWidth() / 2 + this.y - ori_ball.y, worldView.getHeight() / 2 + this.x - ori_ball.x, ballRadius, paint);
            paint.setColor(Color.BLUE);
            canvas.drawCircle(worldView.getWidth() / 2 + this.y - ori_ball.y, worldView.getHeight() / 2 + this.x - ori_ball.x, ballRadius - 5, paint);
            canvas.drawText(playerName, worldView.getWidth()/2 + this.y - ori_ball.y, worldView.getHeight() / 2 + this.x - ori_ball.x, paintName);
        }
    }

//    public void sendBluetoothMessage(){
//        try{
//            StringBuffer sb = new StringBuffer();
//            sb.append("ShowOnScreen");
//            sb.append(",");
//            sb.append(String.valueOf(screenWidth));
//            sb.append(",");
//            sb.append(String.valueOf(screenHeight));
//            sb.append(",");
//            sb.append(String.valueOf(x));
//            sb.append(",");
//            sb.append(String.valueOf(y));
//            sb.append(",");
//            sb.append(String.valueOf(xSpeed));
//            sb.append(",");
//            sb.append(String.valueOf(ySpeed));
//            sb.append("\n");
//
//            worldView.onScreen=false;
//            worldView.outputStream.write(sb.toString().getBytes());
//            worldView.outputStream.flush();
//
//        } catch (IOException e) {
////           e.printStackTrace();
//        }
//    }



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

    public WorldView getWorldView() {
        return worldView;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setPreweight(float preweight) {
        this.preweight = preweight;
    }
//


    public int getScore() {
        return score;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

//    public void updatePosition(Ball oriball) {
//        float rex=x-oriball.x;
//        float rey=y-oriball.y;
//        if(x>=globalWidth-540+rex)
//            x=540+rex;
//        if(y>=globalHeight -960+rey)
//            y=960+rey;
//        if(x<540+rex)
//            x=globalWidth-540+rex;
//        if(y<960+rey)
//            y=globalHeight -960+rey;
//    }


    public void updatePosition(Ball oriball) {
        float rex=x-oriball.x;
        float rey=y-oriball.y;
        if(x>=globalWidth-worldView.getHeight()/2+rex)
            x=worldView.getHeight()/2+rex;
        if(y>=globalHeight -worldView.getWidth() / 2+rey)
            y=worldView.getWidth() / 2+rey;
        if(x<worldView.getHeight()/2+rex)
            x=globalWidth-worldView.getHeight()/2+rex;
        if(y<worldView.getWidth() / 2+rey)
            y=globalHeight -worldView.getWidth() / 2+rey;
    }

    public void updataScore(){
        score=(int)Math.round((Math.pow(weight,2)-2500)/900);
    }
}
