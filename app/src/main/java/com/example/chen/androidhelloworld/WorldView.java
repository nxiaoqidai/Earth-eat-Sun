package com.example.chen.androidhelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    public boolean onScreen = true;
    public boolean connected = false;

    private int width;
    private int height;
    public OutputStream outputStream;

    public WorldView (Context context, AttributeSet attrs) {
        super(context, attrs);
        AccelerometerSensor aSensor = new AccelerometerSensor(this, context);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public void run(){
        while(running){
            Canvas canvas=null;
            try{
                canvas=surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder){
                    this.onDraw(canvas);
                    ball.onDraw(canvas);
                }

            }finally {
                if (canvas!=null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }try{
                Thread.sleep(10);
            }catch (Exception e) {}

        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder){
        this.surfaceHolder=surfaceHolder;
        this.running=true;
        width=getWidth();
        height=getHeight();
        ball = new Ball(this,null,width,height);
        Thread t=new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){}

    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.BLUE);       //draw background.
    }
}
