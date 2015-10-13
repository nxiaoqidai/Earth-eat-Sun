package com.example.chen.androidhelloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by chenxixiang Hao on 10/8/2015.
 */
public class AIBall extends Ball{
    public AIBall(WorldView worldView, Bitmap bitmap, int screenHeight, int screenWidth) {
    super( worldView, bitmap, screenHeight, screenWidth);
        Random random=new Random();
        x=1080+random.nextFloat()*(screenWidth-1080*2);
        y=1920+random.nextFloat()*(screenHeight-1920*2);
    }

    public void moveAIMethod(Ball ball,Food[] food,Obstacle[] obstacle){
        float distance;
        distance=(float)(Math.sqrt(Math.pow(ball.x-x,2)+Math.pow(ball.y-y,2)));
        if(distance>540){
            float fdis=Float.MAX_VALUE;
            int num=0;
            for(int i=0;i<food.length;i++) {
                float temp = (float) (Math.sqrt(Math.pow(food[i].x - x, 2) + Math.pow(food[i].y - y, 2)));
                if(temp<fdis) {
                    fdis = temp;
                    num=i;
                }
            }

            float xaxis=(food[num].x-x)/fdis;
            float yaxis=(food[num].y-y)/fdis;
            controlSpeed(xaxis,yaxis);
        }else{
            if(this.getBallRadius()<=ball.getBallRadius()){
                float xaxis=(x-ball.x)/distance;
                float yaxis=(y-ball.y)/distance;
                controlSpeed(xaxis,yaxis);
            }else{
                float xaxis=(ball.x-x)/distance;
                float yaxis=(ball.y-y)/distance;
                controlSpeed(xaxis,yaxis);
            }
        }
    }

    public void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        updatePhysics();
        if(this.getWorldView().onScreen){
            moveBall();
      //      updatePosition(x, y);
            canvas.drawCircle(x+540-WorldView.getXposition(), y+960-WorldView.getYposition(),this.getBallRadius(), paint);
            paint.setColor(Color.RED);
            canvas.drawCircle(x+540-WorldView.getXposition(), y+960-WorldView.getYposition(),this.getBallRadius()-5, paint);
        }
    }


}
