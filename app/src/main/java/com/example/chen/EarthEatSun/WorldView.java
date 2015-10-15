package com.example.chen.EarthEatSun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by chenxixiang on 15/9/24.
 */
public class WorldView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder;
    private boolean running = false;
    public BallLegion myLegion;
    public ArrayList<AIBallLegion> aiLegion = new ArrayList<AIBallLegion>();

    public Food[] food = new Food[50];
    public Shelter[] shelter = new Shelter[10];

    private int width;
    private int height;

    private int globalWidth = 1920 * 5;
    private int globalHeight = 1080 * 5;

    private static float xposition;
    private static float yposition;

    private Context context;

    private boolean darkMode = false;
    private boolean acceleSensor = false;
    private boolean lightSensor = false;

    private String username;
    private long time;

    public WorldView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        setFocusable(true);
        this.context = context;
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.running = true;

        if (acceleSensor) {
            AccelerometerSensor aSensor = new AccelerometerSensor(this, context);
        }
        if (lightSensor) {
            LightSensor lightSensor = new LightSensor(this, context);
        }

        time = System.currentTimeMillis();

        width = this.getWidth();
        height = this.getHeight();

        myLegion = new BallLegion(this, globalHeight, globalWidth, height, width, username);

        for (int i = 0; i < 8; i++) {
            Random random = new Random();
            AIBallLegion newai = new AIBallLegion(this, globalHeight, globalWidth, height, width, "AI " + String.valueOf(i));
            newai.ball.x = width + (globalWidth - 2 * width) * random.nextFloat();
            newai.ball.y = width + (globalWidth - 2 * width) * random.nextFloat();
            aiLegion.add(newai);
        }

        for (int i = 0; i < food.length; i++)
            food[i] = new Food(this, globalHeight, globalWidth);

        for (int i = 0; i < shelter.length; i++)
            shelter[i] = new Shelter(this, globalHeight, globalWidth);

        Thread t = new Thread(this);
        t.start();
    }

    public void run() {


        while (running) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    dataCalculator(canvas);
                }

            } finally {
                if (canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
            try {

                Thread.sleep(10);
            } catch (Exception e) {
            }

        }
    }

    public void dataCalculator(Canvas canvas) {

        xposition = myLegion.ball.x;
        yposition = myLegion.ball.y;

        this.drawBackground(canvas);

        for (int i = 0; i < food.length; i++)
            food[i].onDraw(canvas);

        myLegion.drawLegion(canvas);

        for (int i = 0; i < aiLegion.size(); i++) {
            aiLegion.get(i).moveAIMethod(myLegion.ball, food, shelter);
            aiLegion.get(i).drawLegion(canvas, myLegion.ball);
        }

        for (int i = 0; i < shelter.length; i++)
            shelter[i].onDraw(canvas);

        this.drawSurface(canvas);

        food = myLegion.eatFood(food);
        shelter = myLegion.shelterDivision(shelter);

        for (int i = 0; i < aiLegion.size(); i++) {
            food = aiLegion.get(i).eatFood(food);
            shelter = aiLegion.get(i).shelterDivision(shelter);
        }

        battleField();
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }


    protected void drawBackground(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(80);
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        if (this.darkMode)
            canvas.drawColor(Color.WHITE);
        else
            canvas.drawColor(Color.BLACK);

        for (int i = 0; i <= globalHeight; i = i + 72) {
            canvas.drawLine(0, i + height / 2 - yposition, width, i + height / 2 - yposition, paint);
        }
        for (int j = 0; j <= globalWidth; j = j + 72) {
            canvas.drawLine(j + width / 2 - xposition, 0, j + width / 2 - xposition, height, paint);
        }
    }

    protected void drawSurface(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(80);
        paint.setStrokeWidth((float) 2.0);
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        long dif_time = System.currentTimeMillis() - time;
        long day = dif_time / (24 * 60 * 60 * 1000);
        long hour = (dif_time / (60 * 60 * 1000) - day * 24);
        long min = ((dif_time / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (dif_time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        String ss;
        String mm;
        if (s < 10)
            ss = "0" + String.valueOf(s);
        else
            ss = String.valueOf(s);
        if (min < 10)
            mm = "0" + String.valueOf(min);
        else
            mm = String.valueOf(min);

        String distime = "Time: " + mm + ":" + ss;
        paint.setTextSize(60);
        canvas.drawText(distime, 50, 100, paint);

        String ballsize = "Size: " + String.valueOf((int) myLegion.ball.getWeight());
        canvas.drawText(ballsize, 50, height - 120, paint);

        paint.setTextSize(80);

        String ballscore = "Score: " + String.valueOf(myLegion.getScore());
        canvas.drawText(ballscore, 50, height - 50, paint);

        paint.setAlpha(75);
        canvas.drawRect(width - 400, 30, width - 30, 600, paint);
        if (this.darkMode)
            paint.setColor(Color.BLACK);
        else
            paint.setColor(Color.GRAY);

        paint.setTextSize(65);

        canvas.drawText("LeaderBoard", width - 400, 100, paint);
        paint.setTextSize(50);

        int[] score = new int[aiLegion.size() + 1];
        int[] index = new int[aiLegion.size() + 1];
        score[0] = myLegion.getScore();
        for (int i = 0; i < aiLegion.size(); i++)
            score[i + 1] = aiLegion.get(i).getScore();

        for (int i = 0; i < score.length; i++) {
            int max = score[0];
            int maxindex = 0;
            for (int j = 0; j < score.length; j++)
                if (score[j] > max) {
                    max = score[j];
                    maxindex = j;
                }
            score[maxindex] = -1;
            index[i] = maxindex;

        }

        for (int i = 0; i < index.length; i++) {
            if (index[i] == 0)
                canvas.drawText(myLegion.getPlayerName() + ": " + String.valueOf(myLegion.getScore()), width - 380, 150 + 50 * i, paint);
            else
                canvas.drawText(aiLegion.get(index[i] - 1).getPlayerName() + ": " + String.valueOf(aiLegion.get(index[i] - 1).getScore()), width - 380, 150 + 50 * i, paint);
        }
    }


    public void battleField() {
        ArrayList<Ball> allball = new ArrayList<Ball>();
        ArrayList<Ball> comball;
        allball.add(myLegion.ball);
        allball.addAll(myLegion.subball);
        for (int i = 0; i < aiLegion.size(); i++) {
            allball.add(aiLegion.get(i).ball);
            allball.addAll(aiLegion.get(i).subball);
        }
        comball = allball;

        for (int i = 0; i < allball.size() - 1; i++) {
            for (int j = i + 1; j < comball.size(); j++)
                if (i != j) {
                    float distance = (float) (Math.sqrt(Math.pow(allball.get(i).x - comball.get(j).x, 2) + Math.pow(allball.get(i).y - comball.get(j).y, 2)));
                    float ar = allball.get(i).getBallRadius();
                    float br = comball.get(j).getBallRadius();
                    boolean flag1 = false;
                    boolean flag2 = false;

                    if ((distance < ar + br - 55) && (!(allball.get(i).getName().equals(comball.get(j).getName()))))
                        flag1 = true;
                    if (ar > br)
                        flag2 = true;
                    if (flag1) {
                        reviseBattleField(allball.get(i), comball.get(j), flag2);
                        return;
                    }
                }
        }
    }

    public void reviseBattleField(Ball balla, Ball ballb, boolean flag) {
        Ball a;
        Ball b;
        if (flag) {
            a = balla;
            b = ballb;
        } else {
            a = ballb;
            b = balla;
        }
        if (myLegion.ball.equals(a))
            myLegion.ball.setBallRadius((float) (Math.sqrt(Math.pow(myLegion.ball.getBallRadius(), 2) + Math.pow(b.getBallRadius(), 2))));
        if (myLegion.ball.equals(b)) {


            Intent exit = new Intent();
            exit.setClass(context, LoginActivity.class);
            exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(exit);
            System.exit(0);
        }

        for (int i = 0; i < myLegion.subball.size(); i++) {
            if (myLegion.subball.get(i).equals(a))
                myLegion.subball.get(i).setBallRadius((float) (Math.sqrt(Math.pow(myLegion.subball.get(i).getBallRadius(), 2) + Math.pow(b.getBallRadius(), 2))));

        }

        for (int i = 0; i < aiLegion.size(); i++) {
            if (aiLegion.get(i).ball.equals(a))
                aiLegion.get(i).ball.setBallRadius((float) (Math.sqrt(Math.pow(aiLegion.get(i).ball.getBallRadius(), 2) + Math.pow(b.getBallRadius(), 2))));

            for (int j = 0; j < aiLegion.get(i).subball.size(); j++) {
                if (aiLegion.get(i).subball.get(j).equals(a))
                    aiLegion.get(i).subball.get(j).setBallRadius((float) (Math.sqrt(Math.pow(aiLegion.get(i).subball.get(j).getBallRadius(), 2) + Math.pow(b.getBallRadius(), 2))));
            }

        }
        for (int i = 0; i < myLegion.subball.size(); i++)
            if (myLegion.subball.get(i).equals(b)) {
                myLegion.subball.remove(i);
                return;
            }

        for (int i = 0; i < aiLegion.size(); i++) {

            if (aiLegion.get(i).ball.equals(b)) {
                aiLegion.remove(i);
                return;
            }

            for (int j = 0; j < aiLegion.get(i).subball.size(); j++)
                if (aiLegion.get(i).subball.get(j).equals(b)) {
                    aiLegion.get(i).subball.remove(j);
                    return;
                }
        }
    }


    public static float getXposition() {
        return xposition;
    }

    public static float getYposition() {
        return yposition;
    }

    public int getGlobalWidth() {
        return globalWidth;
    }

    public int getGlobalHeight() {
        return globalHeight;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void setLightSensor(boolean lightSensor) {
        this.lightSensor = lightSensor;
    }

    public void setAcceleSensor(boolean acceleSensor) {
        this.acceleSensor = acceleSensor;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

