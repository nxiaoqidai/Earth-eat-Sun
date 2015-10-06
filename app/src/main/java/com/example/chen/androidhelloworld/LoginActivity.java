package com.example.chen.androidhelloworld;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by chenxixiang on 15/10/5.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
    }
}
