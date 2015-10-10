package com.example.chen.androidhelloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

/**
 * Created by chenxixiang on 15/10/5.
 */
public class LoginActivity extends Activity {

    private EditText editUsername;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        editUsername=(EditText) findViewById(R.id.editText1);
        Button startButton = (Button) findViewById(R.id.button1);
        final RadioButton singlePlayerButton = (RadioButton) findViewById(R.id.singleplayer);
        final RadioButton toughButton =(RadioButton) findViewById(R.id.radioButton3);
        final Switch nightMareModeSwitch =(Switch) findViewById(R.id.nightmare);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isSingle;
                Boolean isNightmare=true;
                Boolean isGravity=true;

                if (editUsername.getText().toString().equals(""))
                    username="Anonymous";
                else
                    username = editUsername.getText().toString();


                isSingle = singlePlayerButton.isChecked();

                isGravity = !toughButton.isChecked();
                isNightmare = nightMareModeSwitch.isChecked();

                PlayerInfo playerInfo=new PlayerInfo(username,isSingle,isNightmare,isGravity);
                Bundle bundle = new Bundle();
                bundle.putSerializable("playerInfo", playerInfo);

                Intent startGameIntent = new Intent(LoginActivity.this, MainActivity.class);
                startGameIntent.putExtras(bundle);
                LoginActivity.this.startActivity(startGameIntent);
            }
        });
    }

    public void singlePlayer(View view){

    }

    public void nightmareMode(View view){

    }

}
