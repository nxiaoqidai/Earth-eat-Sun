package com.example.chen.androidhelloworld;

import android.content.Context;
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
public class WorldView extends SurfaceView implements SurfaceHolder.Callback,Runnable{


    private SurfaceHolder surfaceHolder;
    private boolean running = false;
    public ArrayList<Ball> ball=new ArrayList<Ball>();
    public ArrayList<AIBall> aiball=new ArrayList<AIBall>();


    public Food[] food= new Food[50];
    public Obstacle[] obstacle = new Obstacle[10];
    //    public Food[] food = new ArrayList<Food>();
    public boolean onScreen = true;
    public boolean connected = false;

    private int width;          //short screen/ x / 宽度
    private int height;         //long screen/ y / 长度

    private int globalWidth=1080*5;
    private int globalHeight=1920*5;

    private static float xposition;
    private static float yposition;

    private boolean darkMode=true;

    public String data = "";

    private String username;
    ArrayList<Ball> speedball= new ArrayList<Ball>();
    ArrayList<AIBall> speedaiball = new ArrayList<AIBall>();
    ArrayList<Integer> splitnum= new ArrayList<Integer>();

    public OutputStream outputStream;

    public WorldView (Context context, AttributeSet attrs) {
        super(context, attrs);
        AccelerometerSensor aSensor = new AccelerometerSensor(this, context);
        LightSensor lightSensor = new LightSensor(this,context);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    //create surface & ball
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        this.surfaceHolder=surfaceHolder;
        this.running=true;

        width=this.getWidth();
        height=this.getHeight();

        Ball firstball=new Ball(this,null,globalHeight,globalWidth);
        ball.add(firstball);

        if(!connected) {
            for (int i = 0; i < food.length; i++)
                food[i] = new Food(this, null, globalHeight, globalWidth);

            for (int i = 0; i < obstacle.length; i++)
                obstacle[i] = new Obstacle(this, null, globalHeight, globalWidth);

        }


//        for(int i=0;i<2;i++)
//            aiball.add(new AIBall(this,null,globalHeight,globalWidth));

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
                //10帧
                Thread.sleep(10);
            }catch (Exception e) {}

        }
    }

    public void dataCalculator(Canvas canvas){

        xposition=ball.get(0).x;
        yposition=ball.get(0).y;

        this.drawBackground(canvas);

        for(int i=0;i<food.length;i++)
            food[i].onDraw(canvas);

        ball.get(0).onDraw(canvas);

        if(ball.size()>1)
            for(int i=1;i<ball.size();i++)
                if(splitnum.contains(i))
                ball.get(i).subOnDraw(canvas,ball.get(0),false);
                else
                ball.get(i).subOnDraw(canvas,ball.get(0),true);

        for(int i=0;i<aiball.size();i++) {
            aiball.get(i).moveAIMethod(ball.get(0),food, obstacle);
            aiball.get(i).onDraw(canvas);
        }

        for(int i=0;i< obstacle.length;i++)
            obstacle[i].onDraw(canvas);
        eatFood();
        battleField();
        reduceSpeed();
        mergeBall();
        shelterDivision();
        drawSurface(canvas);



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
        paint.setTextSize(80);
        paint.setStrokeWidth((float)2.0);
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        if (this.darkMode)
            canvas.drawColor(Color.WHITE);
        else
            canvas.drawColor(Color.BLACK);
//                canvas.drawCircle(width / 2 + i - yposition, height / 2 + j - xposition, 6, paint);
        for (int i=0;i<=globalHeight; i=i+72) {
            canvas.drawLine(i + width / 2 - yposition, 0, i + width / 2 - yposition, height, paint);
        }
        for (int j=0;j<=globalWidth; j=j+72) {
            canvas.drawLine(0, j + height / 2 - xposition, width, j + height / 2 - xposition, paint);
        }




        }


    protected void drawSurface(Canvas canvas){
        Paint paint=new Paint();
        paint.setTextSize(80);
        paint.setStrokeWidth((float)2.0);
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        String ballscore="Score: " + String.valueOf(ball.get(0).getScore());
        canvas.drawText(ballscore, 50, height - 50, paint);
        paint.setAlpha(75);
        canvas.drawRect(width-400,30,width-30,600,paint);

    }

    public void eatFood(){
        for(int i=0;i<food.length;i++) {
            for (int j = 0; j < ball.size(); j++) {
                if (Math.pow(food[i].x - ball.get(j).x, 2) + Math.pow(food[i].y - ball.get(j).y, 2) <= Math.pow(ball.get(j).getBallRadius(), 2)) {
//                    if (!checkDistance(food[i])) {
                        ball.get(j).setBallRadius((float) (Math.sqrt(Math.pow(ball.get(j).getBallRadius(), 2) + Math.pow(food[i].getRadius(), 2))));
                        calculateWeight();
                        food[i] = new Food(this, null, globalHeight, globalWidth);
//                    }
                }
            }
            for (int k = 0; k < aiball.size(); k++) {
                if (Math.pow(food[i].x - aiball.get(k).x, 2) + Math.pow(food[i].y - aiball.get(k).y, 2) <= Math.pow(aiball.get(k).getBallRadius(), 2)) {
//                    if (!checkDistance(food[i])) {
                        aiball.get(k).setBallRadius((float) (Math.sqrt(Math.pow(aiball.get(k).getBallRadius(), 2) + Math.pow(food[i].getRadius(), 2))));
                        calculateWeight();
                        food[i] = new Food(this, null, globalHeight, globalWidth);
//                    }
                }

            }
        }

    }


    public void battleField(){
        for(int i=0;i<ball.size();i++)
            for(int j=0;j<aiball.size();j++){
                float distance = (float)(Math.sqrt(Math.pow(ball.get(i).x - aiball.get(j).x, 2) + Math.pow(ball.get(i).y - aiball.get(j).y, 2)));
                float br=ball.get(i).getBallRadius();
                float ar=aiball.get(j).getBallRadius();
//                if(ball.get(i).getBallRadius()<aiball.get(j).getBallRadius())
//                    r=ball.get(i).getBallRadius();
//                else
//                    r=aiball.get(j).getBallRadius();
                if(distance<ar+br-10){
                    if(ball.get(i).getBallRadius()>=aiball.get(j).getBallRadius()+10) {
                        ball.get(i).setBallRadius((float) (Math.sqrt(Math.pow(ball.get(i).getBallRadius(), 2) + Math.pow(aiball.get(j).getBallRadius(), 2))));
                        calculateWeight();
                        aiball.remove(j);
                    }
//                    if(aiball.get(j).getBallRadius()>=ball.get(i).getBallRadius()+10){
//                        aiball.get(j).setBallRadius((float) (Math.sqrt(Math.pow(ball.get(i).getBallRadius(), 2) + Math.pow(aiball.get(j).getBallRadius(), 2))));
//                        ball.remove(i);
//                    }


                }
            }
    }



    public void spaceDivision(){
        int size=ball.size();
        for(int i=0;i<size;i++) {
            if(ball.get(i).getBallRadius()>75) {
                ball.get(i).setBallRadius((float) (ball.get(i).getBallRadius() * 0.707));
                Ball newball = new Ball(this, null, globalHeight, globalWidth);
                newball.setBallRadius(ball.get(i).getBallRadius());
                float speed = (float) (Math.sqrt(Math.pow(ball.get(i).getxSpeed(), 2) + Math.pow(ball.get(i).getySpeed(), 2)));
                newball.x = ball.get(i).x + ball.get(i).getBallRadius() * ball.get(i).getxSpeed() / speed;
                newball.y = ball.get(i).y + ball.get(i).getBallRadius() * ball.get(i).getySpeed() / speed;
                newball.setxSpeed(50 * ball.get(i).getxSpeed() / speed);
                newball.setySpeed(50 * ball.get(i).getySpeed() / speed);

                splitnum.add(ball.size());
                ball.add(newball);
                speedball.add(newball);
            }
        }

    }



    public void shelterDivision(){
        for(int i=0;i<ball.size();i++)
            if(ball.get(i).getBallRadius()>100)
                for(int j=0;j< obstacle.length;j++) {

                    if (Math.pow(obstacle[j].x - ball.get(i).x, 2) + Math.pow(obstacle[j].y - ball.get(i).y, 2) <= Math.pow(ball.get(i).getBallRadius(), 2)) {
                        float fnum=(float)(Math.pow(ball.get(i).getBallRadius(),2)/2500);
                        float inum=Math.round(fnum);
                        ball.get(i).setBallRadius(50);

                        for(int k=0;k<inum;k++){
                            Ball dball = new Ball(this,null,globalHeight,globalWidth);
                            Random random=new Random();

                            float new_x=random.nextFloat()*100-50;
                            dball.x=ball.get(i).x+new_x;
                            float new_y=(float)Math.sqrt(2500-Math.pow(new_x,2));
                            if(random.nextBoolean())
                                new_y=-new_y;

                                dball.y=ball.get(i).y+random.nextFloat()*new_y;

//                            dball.x=ball.get(i).x;
//                            dball.y=ball.get(i).y;
                            float new_xs=random.nextFloat();
                            float new_ys=(float)Math.sqrt(1-Math.pow(new_xs,2));

                            if(new_x<0)
                                new_xs=-Math.abs(new_xs);
                            else
                                new_xs=Math.abs(new_xs);

                            if(new_y<0)
                                new_ys=-Math.abs(new_ys);
                            else
                                new_ys=Math.abs(new_ys);

                            dball.setxSpeed(ball.get(i).getxSpeed()+25*new_xs);
                            dball.setySpeed(ball.get(i).getySpeed()+25*new_ys);

                            dball.setPreweight(0);
                            calculateWeight();
                            dball.updateSpeed();
                            obstacle[j]=new Obstacle(this,null,globalHeight,globalWidth);
                            speedball.add(dball);
                            splitnum.add(ball.size());
                            ball.add(dball);
                        }
                    }
                }
        calculateWeight();
        for(int i=0;i<aiball.size();i++)
            if(aiball.get(i).getBallRadius()>100)
                for(int j=0;j< obstacle.length;j++) {

                    if (Math.pow(obstacle[j].x - aiball.get(i).x, 2) + Math.pow(obstacle[j].y - aiball.get(i).y, 2) <= Math.pow(aiball.get(i).getBallRadius(), 2)) {
                        float fnum=(float)(Math.pow(aiball.get(i).getBallRadius(),2)/2500);
                        float inum=Math.round(fnum);
                        aiball.get(i).setBallRadius(50);
                        for(int k=0;k<inum;k++){
                            AIBall dball = new AIBall(this,null,globalHeight,globalWidth);
                            Random random=new Random();

                            float new_x=random.nextFloat()*200-100;
                            dball.x=aiball.get(i).x+new_x;
                            float new_y=(float)Math.sqrt(10000-Math.pow(new_x,2));
                            if(random.nextBoolean())
                                dball.y=aiball.get(i).y+new_y;
                            else
                                dball.y=aiball.get(i).y-new_y;
                            dball.setxSpeed(aiball.get(i).getxSpeed());
                            dball.setySpeed(aiball.get(i).getySpeed());
                            dball.setPreweight(0);
                            calculateWeight();
                            dball.updateSpeed();
                            obstacle[j]=new Obstacle(this,null,globalHeight,globalWidth);
                            speedaiball.add(dball);
                            aiball.add(dball);
                        }
                    }
                }


    }

    public void reduceSpeed(){
        for(int i=0;i<speedball.size();i++) {
            float distance=(float)Math.sqrt(Math.pow(speedball.get(i).x-ball.get(0).x,2)+Math.pow(speedball.get(i).y-ball.get(0).y,2));
            if (distance>=500){
                float xp=ball.get(0).getxSpeed();
                float yp=ball.get(0).getySpeed();

                speedball.get(i).setxSpeed(xp);
                speedball.get(i).setySpeed(yp);

            }
        }
    }

    public void mergeBall(){
        for(int i=0;i<speedball.size();i++) {
            float distance = (float) Math.sqrt(Math.pow(speedball.get(i).x - ball.get(0).x, 2) + Math.pow(speedball.get(i).y - ball.get(0).y, 2));
            if(distance<ball.get(0).getBallRadius()-40){
                ball.get(0).setBallRadius((float) (Math.sqrt(Math.pow(ball.get(0).getBallRadius(), 2) + Math.pow(speedball.get(i).getBallRadius(), 2))));

                for(int j=1;j<ball.size();j++)
                    if(ball.get(j).equals(speedball.get(i)))
                        ball.remove(j);
                speedball.remove(i);
            }
        }
    }

    public void calculateWeight(){
        float weight;
        float acreage=0;
        for(int i=0;i<ball.size();i++)
            acreage=acreage+(float)(Math.pow(ball.get(i).getBallRadius(),2));
        weight=(float)Math.sqrt(acreage);
        for(int j=0;j<ball.size();j++)
            ball.get(j).setWeight(weight);
        for(int k=0;k<speedball.size();k++)
            speedball.get(k).setWeight(weight);
    }



    public static float getXposition() {
        return xposition;
    }

    public static float getYposition() {
        return yposition;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }


}

