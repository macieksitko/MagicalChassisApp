package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private LinearLayout tap;
    TinyDB isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        isConnected = new TinyDB(this);
        isConnected.putBoolean("isConnected",false);
        tap = (LinearLayout) findViewById(R.id.textHome);
        tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(0,0);
                openActivity2();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getApplication().closeBluetoothConnection();
        isConnected.putString("address", null);
    }

    public void openActivity2() {
        Intent intent = new Intent (this, Menu.class);
        startActivity(intent);

    }
}
