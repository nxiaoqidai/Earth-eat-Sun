package com.example.chen.androidhelloworld;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9834F8");
    private static final int REQUEST_ENABLE_BT =1;
    private BluetoothAdapter mBluetoothAdapter;
    private Thread myServer;
    private boolean isServer= true;
    private WorldView worldView;

    public BluetoothAdapter getMBluetoothAdapter(){
        return mBluetoothAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        worldView = (WorldView) findViewById(R.id.worldView);
        startBluetooth();
    }

    private final BroadcastReceiver mReviver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name =device.getName();
                Log.d("BlueTooth Device: ", name);
            }
        }

    };






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

        Intent discoveralbeIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveralbeIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoveralbeIntent);

        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReviver,filter);

        myServer=new ServerThread();
        myServer.start();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size()>0){
            for (BluetoothDevice device:pairedDevices){
                String deviceName=device.getName();
                if (deviceName.equals("chenxixiang")){
                    isServer=false;
                    worldView.onScreen=false;
                    ConnectThread myConnection = new ConnectThread(device);
                    myConnection.start();
                    worldView.connected=true;
                }
                Log.d("Bluetooth Device: ",deviceName);
            }
        }


    }

    private class ServerThread extends Thread{
        private final BluetoothServerSocket myServerSocket;

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
                    mBluetoothAdapter.cancelDiscovery();
                }catch(IOException e){
                    Log.e("Bluetooth","Connection failed");
                    break;
                }

                try{
                    inputStream=connectSocket.getInputStream();
                    outputStream=connectSocket.getOutputStream();
                    worldView.outputStream=outputStream;
                    worldView.connected=true;
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                    while ((line=br.readLine())!=null){
                        try{
                            if(line.startsWith("ShowOnScreen")){
                                worldView.onScreen=true;
                                List<String> coords= Arrays.asList(line.split(","));

                                float screenWidth=Float.parseFloat(coords.get(1));
                                float screenHeight=Float.parseFloat(coords.get(2));
                                float x=Float.parseFloat(coords.get(3));
                                float y=Float.parseFloat(coords.get(4));
                                float Xspeed=Float.parseFloat(coords.get(5));
                                float Yspeed=Float.parseFloat(coords.get(6));

                                worldView.ball.resetCoordinate(screenWidth, screenHeight, x, y, Xspeed, Yspeed);

                            }

                            Log.e("Bluetooth","Recieve: "+line);
                        }catch (Exception e){
                            Log.e("Bluetooth","Disconnected");
                            break;
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    break;
                }

            }
        }
    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mySocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket temp=null;
            try{
                temp=device.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e){
                Log.e("Bluetooth","Could not connect");
            }
            mySocket=temp;
        }

        public void run(){
            InputStream inputStream=null;
            OutputStream outputStream=null;
            mBluetoothAdapter.cancelDiscovery();

            try{
                mySocket.connect();
            }catch (IOException e){
                Log.e("Bluetooth",this.getName()+": Could not establish connection with device");
                try{
                    mySocket.close();
                }catch (IOException e1){
                    Log.e("Bluetooth",this.getName()+": could not close socket",e1);
                    this.destroy();
                }
            }

            String line ="";
            try{
                inputStream=mySocket.getInputStream();
                outputStream=mySocket.getOutputStream();
                BufferedReader br =new BufferedReader(new InputStreamReader(inputStream));
                worldView.outputStream=outputStream;

                while((line=br.readLine())!=null){
                    try{
                        Log.e("Bluetooth","Received: "+line);
                        if(line.startsWith("ShowOnScreen")){
                            worldView.onScreen=true;
                            List<String> coords=Arrays.asList(line.split(","));
                            float screenWidth = Float.parseFloat(coords.get(1));
                            float screenHeight = Float.parseFloat(coords.get(2));
                            float x = Float.parseFloat(coords.get(3));
                            float y = Float.parseFloat(coords.get(4));
                            float xSpeed = Float.parseFloat(coords.get(5));
                            float ySpeed = Float.parseFloat(coords.get(6));

                            worldView.ball.resetCoordinate(screenWidth,screenHeight,x,y,xSpeed,ySpeed);

                        }
                    }catch (Exception e3){
                        Log.e("Bluetooth","Disconnected");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void cancel(){
            try{
                mySocket.close();

            }catch (IOException e){
                Log.e("Bluetooth", this.getName() + ": Could not close socket");
            }
        }
    }

}

