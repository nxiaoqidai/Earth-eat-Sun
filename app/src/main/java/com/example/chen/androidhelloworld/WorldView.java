package com.example.chen.androidhelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.OutputStream;

/**
 * Created by chenxixiang on 15/9/24.
 */
public class WorldView extends SurfaceView implements SurfaceHolder.Callback,Runnable{


    private SurfaceHolder surfaceHolder;
    private boolean running = false;
    public Ball ball;
    public Food[] foods= new Food[50];
    public Obstacle[] obstacles = new Obstacle[5];

    public boolean onScreen = true;
    public boolean connected = false;

    private int screenWidth;          //short screen/ x / 宽度
    private int screenHeight;         //long screen/ y / 长度

    private int worldWidth;
    private int worldHeight;

    private float publicX;      //global x speed;
    private float publicY;      //global y speed;

    public OutputStream outputStream;

    public WorldView (Context context, AttributeSet attrs) {
        super(context, attrs);
        AccelerometerSensor aSensor = new AccelerometerSensor(this, context);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    //create surface & ball
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        this.surfaceHolder=surfaceHolder;
        this.running=true;

        this.worldWidth=9000;
        this.worldHeight=15000;
        screenWidth=this.getWidth();
        screenHeight=this.getHeight();

        //initialize the foods
        for (int i=0;i<50;i++)
            foods[i]=new Food(this,worldWidth,worldHeight);

        //initialize the obstacles
        for (int j=0; j<5;j++)
            obstacles[j]=new Obstacle(this,worldWidth,worldHeight);

        ball = new Ball(this,null,worldWidth,worldHeight);

        //UI Thread;
        Thread t=new Thread(this);
        t.start();
    }

    public void run(){


        while(running){
            Canvas canvas=null;
            try{
                canvas=surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder){
                    dataCalculator(canvas);

                }

            }finally {
                if (canvas!=null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }try{
                //100 frame/s
                Thread.sleep(10);
            }catch (Exception e) {}

        }
    }

    public void dataCalculator(Canvas canvas){
        publicX=ball.getX();
        publicY=ball.getY();

        this.drawBackground(canvas);

        for(int i=0;i<obstacles.length;i++)
            obstacles[i].onDraw(canvas);

        for(int j=0;j<foods.length;j++)
            foods[j].onDraw(canvas);
        ball.onDraw(canvas);
        eatFood(ball,foods);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    //touch the home button
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //touch the back button
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){

    }

    //draw background.
    protected void drawBackground(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        //draw background
        canvas.drawColor(Color.WHITE);

        //draw coordinate point
        for (int i=0;i<=worldWidth; i=i+150){
            for (int j=0;j<=worldHeight; j=j+150){
                canvas.drawCircle(i-publicX+screenWidth/2,j-publicY+screenHeight/2,5,paint);
            }
        }
    }

    public void eatFood(Ball ball, Food[] food){
        for(int i=0;i<food.length;i++) {
            if (Math.pow(food[i].x - ball.x, 2) + Math.pow(food[i].y- ball.y, 2) <= Math.pow(ball.getBallRadius(), 2)) {
                ball.setBallRadius((float)(Math.sqrt(Math.pow(ball.getBallRadius(), 2) + Math.pow(food[i].getRadius(), 2))));

                food[i] = new Food(this,worldWidth,worldHeight);
            }
        }
    }

    public float getPublicX() {
        return publicX;
    }

    public float getPublicY() {
        return publicY;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
