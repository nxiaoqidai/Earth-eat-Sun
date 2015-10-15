package com.example.chen.EarthEatSun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by chenxixiang  on 10/8/2015.
 */
public class BallLegion {
    public Ball ball;
    public ArrayList<Ball> subball = new ArrayList<Ball>();
    private WorldView worldView;
    private int score;
    private String playerName;

    public BallLegion(WorldView view, int gH, int gW, int sH, int sW, String s) {
        this.worldView = view;
        this.playerName = s;
        ball = new Ball(gH, gW, sH, sW, playerName);
    }

    public void drawLegion(Canvas canvas) {
        calculateWeight();
        updateSubBall();
        ball.moveBall();
        this.onDraw(canvas, ball.getBallRadius());

        for (int i = 0; i < subball.size(); i++) {
            subball.get(i).moveBall();
            subball.get(i).updatePhysics(ball);
            this.onDraw(canvas, ball, subball.get(i), true);
        }
        ball.updatePhysics();
    }

    public void onDraw(Canvas canvas, float ballRadius) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        canvas.drawCircle(worldView.getWidth() / 2, worldView.getHeight() / 2, ballRadius, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(worldView.getWidth() / 2, worldView.getHeight() / 2, ballRadius - 5, paint);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(playerName, worldView.getWidth() / 2, worldView.getHeight() / 2, paint);

    }


    public void onDraw(Canvas canvas, Ball ori_ball, Ball cur_ball, boolean flag) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        canvas.drawCircle(worldView.getWidth() / 2 + cur_ball.x - ori_ball.x, worldView.getHeight() / 2 + cur_ball.y - ori_ball.y, cur_ball.getBallRadius(), paint);
        if (!flag)
            paint.setColor(Color.BLUE);
        else
            paint.setColor(Color.YELLOW);

        canvas.drawCircle(worldView.getWidth() / 2 + cur_ball.x - ori_ball.x, worldView.getHeight() / 2 + cur_ball.y - ori_ball.y, cur_ball.getBallRadius() - 5, paint);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(playerName, worldView.getWidth() / 2 + cur_ball.x - ori_ball.x, worldView.getHeight() / 2 + cur_ball.y - ori_ball.y, paint);
    }

    public Food[] eatFood(Food[] food) {

        for (int i = 0; i < food.length; i++) {

            if (Math.pow(food[i].x - ball.x, 2) + Math.pow(food[i].y - ball.y, 2) <= Math.pow(ball.getBallRadius(), 2)) {
                ball.setBallRadius((float) (Math.sqrt(Math.pow(ball.getBallRadius(), 2) + Math.pow(food[i].getRadius(), 2))));
                calculateWeight();
                food[i] = new Food(worldView, worldView.getGlobalHeight(), worldView.getGlobalWidth());
            }

            for (int j = 0; j < subball.size(); j++) {
                if (Math.pow(food[i].x - subball.get(j).x, 2) + Math.pow(food[i].y - subball.get(j).y, 2) <= Math.pow(subball.get(j).getBallRadius(), 2)) {
                    subball.get(j).setBallRadius((float) (Math.sqrt(Math.pow(subball.get(j).getBallRadius(), 2) + Math.pow(food[i].getRadius(), 2))));
                    calculateWeight();
                    food[i] = new Food(worldView, worldView.getGlobalHeight(), worldView.getGlobalWidth());
                }

            }
        }
        return food;
    }

    public Shelter[] shelterDivision(Shelter[] shelter) {

        for (int i = 0; i < shelter.length; i++) {
            if (ball.getBallRadius() > 180 && Math.pow(shelter[i].x - ball.x, 2) + Math.pow(shelter[i].y - ball.y, 2) <= Math.pow(ball.getBallRadius(), 2)) {
                float num = (float) (Math.pow(ball.getBallRadius(), 2) / 2500);
                float subnum = Math.round(num);
                ball.setBallRadius(50);
                shelter[i] = new Shelter(worldView, worldView.getGlobalHeight(), worldView.getGlobalWidth());
                for (int k = 0; k < subnum; k++)
                    subball.add(createSubBall(ball, false));
            }
            for (int j = 0; j < subball.size(); j++)
                if (subball.get(j).getBallRadius() > 180 && Math.pow(shelter[i].x - subball.get(j).x, 2) + Math.pow(shelter[i].y - subball.get(j).y, 2) <= Math.pow(subball.get(j).getBallRadius(), 2)) {
                    float num = (float) (Math.pow(subball.get(j).getBallRadius(), 2) / 2500);
                    float subnum = Math.round(num);
                    subball.get(j).setBallRadius(50);
                    for (int k = 0; k < subnum; k++) {
                        shelter[i] = new Shelter(worldView, worldView.getGlobalHeight(), worldView.getGlobalWidth());

                        subball.add(createSubBall(subball.get(j), false));
                    }
                }
        }

        calculateWeight();
        return shelter;
    }


    public void spaceDivision() {
        ArrayList<Ball> templist = new ArrayList<Ball>();
        if (ball.getBallRadius() > 90)
            templist.add(createSubBall(ball, true));

        int size = subball.size();
        for (int i = 0; i < size; i++)
            if (subball.get(i).getBallRadius() > 75)
                templist.add(createSubBall(subball.get(i), true));

        subball.addAll(templist);
        calculateWeight();
    }

    public void spaceDivision(Ball myball) {
        ArrayList<Ball> templist = new ArrayList<Ball>();
        if (ball.getBallRadius() > 90 && ball.getBallRadius() > 1.5 * myball.getBallRadius()) {
            ball.setBallRadius((float) (ball.getBallRadius() * 0.707));
            templist.add(createSubBall(ball, true));
        }
        int size = subball.size();
        for (int i = 0; i < size; i++)
            if (subball.get(i).getBallRadius() > 75 && subball.get(i).getBallRadius() > 1.5 * myball.getBallRadius()) {
                subball.get(i).setBallRadius((float) (subball.get(i).getBallRadius() * 0.707));
                templist.add(createSubBall(subball.get(i), true));
            }
        subball.addAll(templist);
        calculateWeight();
    }

    private Ball createSubBall(Ball tempball, boolean flag) {
        Ball newball = new Ball(worldView.getGlobalHeight(), worldView.getGlobalWidth(), worldView.getHeight(), worldView.getWidth(), playerName);
        if (flag) {
            tempball.setBallRadius((float) (tempball.getBallRadius() * 0.707));
            newball.setBallRadius(tempball.getBallRadius());
            float speed = (float) (Math.sqrt(Math.pow(tempball.getxSpeed(), 2) + Math.pow(tempball.getySpeed(), 2)));
            newball.x = tempball.x + tempball.getBallRadius() * tempball.getxSpeed() / speed;
            newball.y = tempball.y + tempball.getBallRadius() * tempball.getySpeed() / speed;
            newball.setxSpeed(50 * tempball.getxSpeed() / speed);
            newball.setySpeed(50 * tempball.getySpeed() / speed);
        } else {
            Random random = new Random();
            float new_x = random.nextFloat() * 220 - 110;
            newball.x = tempball.x + new_x;
            float new_y = (float) Math.sqrt(12100 - Math.pow(new_x, 2));
            if (random.nextBoolean())
                new_y = -new_y;
            newball.y = tempball.y + random.nextFloat() * new_y;
            float new_xs = random.nextFloat();
            float new_ys = (float) Math.sqrt(1 - Math.pow(new_xs, 2));
            if (new_x < 0)
                new_xs = -Math.abs(new_xs);
            else
                new_xs = Math.abs(new_xs);

            if (new_y < 0)
                new_ys = -Math.abs(new_ys);
            else
                new_ys = Math.abs(new_ys);
            newball.setxSpeed(tempball.getxSpeed() + 25 * new_xs);
            newball.setySpeed(tempball.getySpeed() + 25 * new_ys);

        }
        return newball;
    }


    public void updateSubBall() {
        for (int i = 0; i < subball.size(); i++) {
            float distance = (float) Math.sqrt(Math.pow(subball.get(i).x - ball.x, 2) + Math.pow(subball.get(i).y - ball.y, 2));
            if (distance >= 500) {
                float xp = ball.getxSpeed();
                float yp = ball.getySpeed();

                float xaxis = (ball.x - subball.get(i).x) / distance;
                float yaxis = (ball.y - subball.get(i).y) / distance;

                subball.get(i).setxSpeed(xp + 8 * xaxis);
                subball.get(i).setySpeed(yp + 8 * yaxis);
            }

            if (distance < ball.getBallRadius() - 40) {
                ball.setBallRadius((float) (Math.sqrt(Math.pow(ball.getBallRadius(), 2) + Math.pow(subball.get(i).getBallRadius(), 2))));
                subball.remove(i);
            }
        }
    }

    public void calculateWeight() {
        float weight;
        float acreage = 0;
        acreage = acreage + (float) (Math.pow(ball.getBallRadius(), 2));

        for (int i = 0; i < subball.size(); i++)
            acreage = acreage + (float) (Math.pow(subball.get(i).getBallRadius(), 2));
        weight = (float) Math.sqrt(acreage);

        ball.setWeight(weight);
        for (int j = 0; j < subball.size(); j++)
            subball.get(j).setWeight(weight);

    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        score = (int) ((Math.pow(ball.getWeight(), 2) - 2500) / 900);
        return score;
    }
}