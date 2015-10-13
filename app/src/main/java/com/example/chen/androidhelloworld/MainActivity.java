package com.example.chen.androidhelloworld;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by chenxixiang on 15/10/5.
 */

public class MainActivity extends Activity {
    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9834F8");
    private static final int REQUEST_ENABLE_BT =1;
    private BluetoothAdapter mBluetoothAdapter;
    private Thread myServer;
    private Thread myConnection;
    private boolean isServer= false;
    private boolean isSingle= false;
    private String tempName;
    private int screenWidth;
    private int screenHeight;
    private WorldView worldView;
 //   private Ball ball;

    public BluetoothAdapter getMBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenWidth=dm.widthPixels;
        this.screenHeight=dm.heightPixels;

        //worldView = new WorldView(this, null)
        ;

        worldView = (WorldView) findViewById(R.id.worldView);
       

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        PlayerInfo playerInfo = (PlayerInfo) bundle.getSerializable("playerInfo");
        Ball.playerName=playerInfo.getPlayerName();

        if(!isSingle){
//            if(isServer)
                startBluetooth();
//            else
//                startBluetooth();
        }
    }

    public void splitButton(View view){
        worldView.spaceDivision();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xspeed;
        float yspeed;
        float xball=worldView.ball.get(0).x;
        float yball=worldView.ball.get(0).y;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                    reviseSpeedByTouch(event.getX(),event.getY());


                break;
            case MotionEvent.ACTION_MOVE:

                reviseSpeedByTouch(event.getX(),event.getY());


        break;
//            case MotionEvent.ACTION_UP:
//                for(int i=0;i<worldView.ball.size();i++) {
//                    xspeed = (float) (25 * (event.getX() - 540) / Math.sqrt(Math.pow(event.getX() - 540, 2) + Math.pow(event.getY() - 960, 2)));
//                    yspeed = (float) (25 * (event.getY() - 960) / Math.sqrt(Math.pow(event.getX() - 540, 2) + Math.pow(event.getY() - 960, 2)));
//                    worldView.ball.get(i).setxSpeed(xspeed / (1 + (worldView.ball.get(i).getBallRadius() - 40) / 20));
//                    worldView.ball.get(i).setySpeed(yspeed / (1 + (worldView.ball.get(i).getBallRadius() - 40) / 20));
//                }
//                break;


        }
        return super.onTouchEvent(event);
    }

    public void reviseSpeedByTouch(float xTouch,float yTouch){
        float xspeed;
        float yspeed;
        float xball=worldView.ball.get(0).x;
        float yball=worldView.ball.get(0).y;
        yspeed = (float) ( (xTouch - worldView.getWidth()/2) / Math.sqrt(Math.pow(xTouch - worldView.getWidth()/2, 2) + Math.pow(yTouch - worldView.getHeight()/2, 2)));
        xspeed = (float) ( (yTouch - worldView.getHeight()/2) / Math.sqrt(Math.pow(xTouch - worldView.getWidth()/2, 2) + Math.pow(yTouch - worldView.getHeight()/2, 2)));
        worldView.ball.get(0).controlSpeed(xspeed,yspeed);
        for(int i=1;i<worldView.ball.size();i++) {
            yspeed = (float) ( (xTouch - (worldView.getWidth()/2+worldView.ball.get(i).y-yball)) / Math.sqrt(Math.pow(xTouch - (worldView.getWidth()/2 + worldView.ball.get(i).y - yball), 2) + Math.pow(yTouch - (worldView.getHeight()/2+ worldView.ball.get(i).x - xball), 2)));
            xspeed = (float) ( (yTouch - (worldView.getHeight()/2+worldView.ball.get(i).x-xball)) / Math.sqrt(Math.pow(xTouch - (worldView.getWidth()/2 + worldView.ball.get(i).y - yball), 2) + Math.pow(yTouch - (worldView.getHeight()/2 + worldView.ball.get(i).x - xball), 2)));
            worldView.ball.get(i).controlSpeed(xspeed,yspeed);
        }
    }

//    private final BroadcastReceiver mReviver=new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action=intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String name =device.getName();
//                Log.d("BlueTooth Device: ", name);
//            }
//        }
//
//    };


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    private void startBluetooth() {
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }

            myServer = new ServerThread();
            myServer.start();

    }

    private class ServerThread extends Thread{
        private final BluetoothServerSocket myServerSocket;
//        private BluetoothServerSocket otherServerSocket;

        private ServerThread() {
            BluetoothServerSocket temp= null;
            try{
                temp=mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("myServer",MY_UUID);

            }catch (IOException e){
                Log.e("Bluetooth","server establishing failed");
            }
            myServerSocket=temp;
        }

        public void run(){
            Log.e("Bluetooth","Begin waiting for connection");
            BluetoothSocket connectSocket=null;
            InputStream inputStream=null;
            OutputStream outputStream=null;
            String line;

            while(true){
                try{
                    connectSocket=myServerSocket.accept();
                    connectSocket.connect();
 //                   mBluetoothAdapter.cancelDiscovery();
                }catch(IOException e){
                    Log.e("Bluetooth","Connection failed");
                    break;
                }
//                BluetoothSocket tt=null;
                try{
                    inputStream=connectSocket.getInputStream();
                    outputStream=connectSocket.getOutputStream();
//                    worldView.outputStream=outputStream;
                //    worldView.connected=true;
//                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//                    int a=pairedDevices.size();
//                if (pairedDevices.size()>0){
//                    for (BluetoothDevice device:pairedDevices){
//                        tt=device.createRfcommSocketToServiceRecord(MY_UUID);
//                        tt.connect();
//                    if(isServer){
                         String test="ABCDE";
//                    outputStream=tt.getOutputStream();
                        outputStream.write(test.getBytes());
                    outputStream.flush();

//                    Log.e("Bluetooth",this.getName()+": could not close socket",e1);
//                    this.destroy();
//                }
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                    while ((line=br.readLine())!=null)
                        worldView.ball.get(0).playerName=line;

                        }catch (IOException e){
                        e.printStackTrace();
                        break;
                    }
                    }


            }
        }
    }

//    private class ConnectThread extends Thread{
//        private final BluetoothSocket mySocket;
//
//        public ConnectThread() {
//            BluetoothSocket temp=null;
//            try{
//                temp=device.createRfcommSocketToServiceRecord(MY_UUID);
//            }catch (IOException e){
//                Log.e("Bluetooth","Could not connect");
//            }
//            mySocket=temp;
//        }
//
//        public void run(){
//            InputStream inputStream=null;
//            OutputStream outputStream=null;
////            mBluetoothAdapter.cancelDiscovery();
//
//            try{
//                mySocket.connect();
//            }catch (IOException e){
//                Log.e("Bluetooth",this.getName()+": Could not establish connection with device");
//                try{
//                    mySocket.close();
//                }catch (IOException e1){
//                    Log.e("Bluetooth",this.getName()+": could not close socket",e1);
//                    this.destroy();
//                }
//            }
//
//            String line ="";
//            try{
//                inputStream=mySocket.getInputStream();
//                outputStream=mySocket.getOutputStream();
//                BufferedReader br =new BufferedReader(new InputStreamReader(inputStream));
//   //             worldView.outputStream=outputStream;
//
//                while((line=br.readLine())!=null){
//                    try{
//                        Log.e("Bluetooth","Received: "+line);
//                        if(line.startsWith("ShowOnScreen")){
////                            worldView.onScreen=true;
////                            List<String> coords=Arrays.asList(line.split(","));
////                            float screenWidth = Float.parseFloat(coords.get(1));
////                            float screenHeight = Float.parseFloat(coords.get(2));
////                            float x = Float.parseFloat(coords.get(3));
////                            float y = Float.parseFloat(coords.get(4));
////                            float xSpeed = Float.parseFloat(coords.get(5));
////                            float ySpeed = Float.parseFloat(coords.get(6));
//                            worldView.ball.get(0).playerName=line;
//
////                            for(int i=0;i<worldView.ball.size();i++)
////                            worldView.ball.get(i).resetCoordinate(screenWidth,screenHeight,x,y,xSpeed,ySpeed);
//
//                        }
//                    }catch (Exception e3){
//                        Log.e("Bluetooth","Disconnected");
//                    }
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        public void cancel(){
//            try{
//                mySocket.close();
//
//            }catch (IOException e){
//                Log.e("Bluetooth", this.getName() + ": Could not close socket");
//            }
//        }
//    }

//}


