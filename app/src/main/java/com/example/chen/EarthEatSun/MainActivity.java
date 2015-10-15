package com.example.chen.EarthEatSun;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by chenxixiang on 15/10/5.
 */

public class MainActivity extends Activity {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9834F8");
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Thread myServer;
    private Thread myConnection;

    private boolean multiplayer = false;
    private boolean gravity = false;

    private int screenWidth;
    private int screenHeight;
    private WorldView worldView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;

        worldView = (WorldView) findViewById(R.id.worldView);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        PlayerInfo playerInfo = (PlayerInfo) bundle.getSerializable("playerInfo");
        worldView.setUsername(playerInfo.getPlayerName());
        worldView.setLightSensor(playerInfo.getIsNightmare());
        worldView.setDarkMode(!playerInfo.getIsNightmare());
        worldView.setAcceleSensor(playerInfo.getIsGravity());
        this.multiplayer = !playerInfo.getIsSingle();
        this.gravity = playerInfo.getIsGravity();

        if (multiplayer)
            startBluetooth();
    }

    public void splitButton(View view) {
        worldView.myLegion.spaceDivision();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gravity)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    reviseSpeedByTouch(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    reviseSpeedByTouch(event.getX(), event.getY());
                    break;
            }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit the game?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent exit = new Intent();
                            exit.setClass(MainActivity.this, LoginActivity.class);
                            exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(exit);

                            System.exit(0);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    })
                    .show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void reviseSpeedByTouch(float xTouch, float yTouch) {
        float xspeed;
        float yspeed;
        float xball = worldView.myLegion.ball.x;
        float yball = worldView.myLegion.ball.y;
        xspeed = (float) ((xTouch - worldView.getWidth() / 2) / Math.sqrt(Math.pow(xTouch - worldView.getWidth() / 2, 2) + Math.pow(yTouch - worldView.getHeight() / 2, 2)));
        yspeed = (float) ((yTouch - worldView.getHeight() / 2) / Math.sqrt(Math.pow(xTouch - worldView.getWidth() / 2, 2) + Math.pow(yTouch - worldView.getHeight() / 2, 2)));
        worldView.myLegion.ball.controlSpeed(xspeed, yspeed);
        for (int i = 0; i < worldView.myLegion.subball.size(); i++) {
            xspeed = (float) ((xTouch - (worldView.getWidth() / 2 + worldView.myLegion.subball.get(i).x - xball)) / Math.sqrt(Math.pow(xTouch - (worldView.getWidth() / 2 + worldView.myLegion.subball.get(i).x - xball), 2) + Math.pow(yTouch - (worldView.getHeight() / 2 + worldView.myLegion.subball.get(i).y - yball), 2)));
            yspeed = (float) ((yTouch - (worldView.getHeight() / 2 + worldView.myLegion.subball.get(i).y - yball)) / Math.sqrt(Math.pow(xTouch - (worldView.getWidth() / 2 + worldView.myLegion.subball.get(i).x - xball), 2) + Math.pow(yTouch - (worldView.getHeight() / 2 + worldView.myLegion.subball.get(i).y - yball), 2)));
            worldView.myLegion.subball.get(i).controlSpeed(xspeed, yspeed);
        }
    }


    public void startBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        myServer = new ServerThread();
        myServer.start();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                if (deviceName.equals("WenzhuoHao")) {

                    ConnectThread myConnection = new ConnectThread(device);
                    myConnection.start();
                }

                Log.d("Bluetooth Device:", deviceName);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                Log.d("Bluetooth Device:", name);
            }
        }
    };

    private class ServerThread extends Thread {
        private final BluetoothServerSocket myServSocket;

        public ServerThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("myServer", MY_UUID);
            } catch (IOException e) {
                Log.e("Bluetooth", "Server establishing failed");
            }

            myServSocket = tmp;
        }

        public void run() {
            Log.e("Bluetooth", "Begin waiting for connection");
            BluetoothSocket connectSocket = null;
            InputStream inStream = null;
            OutputStream outStream = null;
            String line = "";

            while (true) {
                try {
                    connectSocket = myServSocket.accept();
                    mBluetoothAdapter.cancelDiscovery();
                } catch (IOException e) {
                    Log.e("Bluetooth", "Connection failed");
                    break;
                }

                try {

                    inStream = connectSocket.getInputStream();
                    outStream = connectSocket.getOutputStream();

                    String a = "Connected!";
                    outStream.write(a.toString().getBytes());
                    outStream.flush();

                    BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

                    while ((line = br.readLine()) != null) {
                        try {

                            Log.e("Bluetooth", "Received: " + line);
                        } catch (Exception e3) {
                            Log.e("Bluetooth", "Disconnected");
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mySocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("Bluetooth", "Could not connect");
            }
            mySocket = tmp;
        }

        public void run() {
            InputStream inStream = null;
            OutputStream outStream = null;
            mBluetoothAdapter.cancelDiscovery();

            try {
                mySocket.connect();
            } catch (IOException e) {
                Log.e("Bluetooth", this.getName() + ": Could not establish connection with device");
                try {
                    mySocket.close();
                } catch (IOException e1) {
                    Log.e("Bluetooth", this.getName() + ": could not close socket", e1);
                    this.destroy();
                }
            }

            String line = "";
            try {
                inStream = mySocket.getInputStream();
                outStream = mySocket.getOutputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

                String a = "Connected";
                outStream.write(a.toString().getBytes());
                outStream.flush();

                while ((line = br.readLine()) != null) {
                    try {
                        Log.e("Bluetooth", "Received: " + line);

                    } catch (Exception e3) {
                        Log.e("Bluetooth", "Disconnected");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mySocket.close();
            } catch (IOException e) {
                Log.e("Bluetooth", this.getName() + ": Could not close socket");
            }
        }
    }
}
