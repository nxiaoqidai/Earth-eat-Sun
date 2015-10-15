package com.example.chen.EarthEatSun;

/**
 * Created by chenxixiang on 15/10/2015.
 */
import android.graphics.Canvas;

public class AIBallLegion extends BallLegion {
    public AIBallLegion(WorldView view, int gH, int gW, int sH, int sW, String s) {
        super(view, gH, gW, sH, sW, s);
    }

    public void moveAIMethod(Ball ball, Food[] food, Shelter[] shelter) {
        float distance;
        boolean flag = false;
        float shelter_dis;
        for (int i = 0; i < shelter.length; i++) {
            shelter_dis = (float) (Math.pow(shelter[i].x - ball.x, 2) + Math.pow(shelter[i].y - ball.y, 2));
            if (shelter_dis < 32400) {
                flag = true;
                break;
            }
        }

        distance = (float) (Math.sqrt(Math.pow(ball.x - this.ball.x, 2) + Math.pow(ball.y - this.ball.y, 2)));
        if (distance > 540) {
            float fdis = Float.MAX_VALUE;
            int num = 0;
            for (int i = 0; i < food.length; i++) {
                float temp = (float) (Math.sqrt(Math.pow(food[i].x - this.ball.x, 2) + Math.pow(food[i].y - this.ball.y, 2)));
                if (temp < fdis) {
                    fdis = temp;
                    num = i;
                }
            }

            float xaxis = (food[num].x - this.ball.x) / fdis;
            float yaxis = (food[num].y - this.ball.y) / fdis;
            this.ball.controlSpeed(xaxis, yaxis);

        } else {
            if (this.ball.getBallRadius() <= ball.getBallRadius()) {
                float xaxis = (this.ball.x - ball.x) / distance;
                float yaxis = (this.ball.y - ball.y) / distance;
                this.ball.controlSpeed(xaxis, yaxis);
            } else if (!flag) {
                float xaxis = (ball.x - this.ball.x) / distance;
                float yaxis = (ball.y - this.ball.y) / distance;
                this.ball.controlSpeed(xaxis, yaxis);
                if (distance < 450 && this.ball.getBallRadius() >= ball.getBallRadius() * 3)
                    spaceDivision(ball);
            } else {
                float fdis = Float.MAX_VALUE;
                int num = 0;
                for (int i = 0; i < food.length; i++) {
                    float temp = (float) (Math.sqrt(Math.pow(food[i].x - this.ball.x, 2) + Math.pow(food[i].y - this.ball.y, 2)));
                    if (temp < fdis) {
                        fdis = temp;
                        num = i;
                    }
                }

                float xaxis = (food[num].x - this.ball.x) / fdis;
                float yaxis = (food[num].y - this.ball.y) / fdis;
                this.ball.controlSpeed(xaxis, yaxis);
            }
        }
        this.ball.moveBall();
        for (int i = 0; i < subball.size(); i++)
            subball.get(i).moveBall();
    }


    public void drawLegion(Canvas canvas, Ball myball) {
        calculateWeight();
        updateSubBall();
        this.onDraw(canvas, myball, ball, false);

        for (int i = 0; i < subball.size(); i++) {
            subball.get(i).updatePhysics();
            this.onDraw(canvas, myball, subball.get(i), false);
        }
        ball.updatePhysics();
    }

}
