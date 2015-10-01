package com.example.chen.androidhelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.OutputStream;

/**
 * Created by chenxixiang on 15/9/24.
 */
public class WorldView extends SurfaceView implements SurfaceHolder.Callback,Runnable{


    private SurfaceHolder surfaceHolder;
    private boolean running = false;
    public Ball ball;
    public Food food;
    public boolean onScreen = true;
    public boolean connected = false;

    private int width;          //short screen/ x / 宽度
    private int height;         //long screen/ y / 长度
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


        width=this.getWidth();
        height=this.getHeight();

        food=new Food(this,null,height,width);
        ball = new Ball(this,null,height,width);
        Thread t=new Thread(this);
        t.start();
    }

    public void run(){
        while(running){
            Canvas canvas=null;
            try{
                canvas=surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder){
                    this.drawBackground(canvas);
                    food.onDraw(canvas);
                    ball.onDraw(canvas);

                }

            }finally {
                if (canvas!=null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }try{
                //10帧
                Thread.sleep(10);
            }catch (Exception e) {}

        }
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
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){}

    //draw background.
    protected void drawBackground(Canvas canvas){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        canvas.drawColor(Color.WHITE);

        for (int i=0;i<width; i=i+150){
            for (int j=0;j<height; j=j+150){
                canvas.drawCircle(i,j,6,paint);
            }
        }
    }


}
