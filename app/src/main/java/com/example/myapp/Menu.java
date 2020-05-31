package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomer.fadingtextview.FadingTextView;

import android.os.Handler;

public class Menu extends AppCompatActivity {

    private ImageView bg;
    private LinearLayout textsplash,menuIcons;
    private TextView menuText,connect,calculate,drive;
    private FadingTextView fadingTextView;
    private AlphaAnimation menuIconsAnim;
    private Animation bgmove;

    boolean menuIconsAnimStatus=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    //}
    //@Override
   // protected void onStart() {
        //super.onStart();
        bg = (ImageView) findViewById(R.id.bg);
        textsplash = (LinearLayout) findViewById(R.id.textHome);
        menuIcons = (LinearLayout) findViewById(R.id.menuIcons);
        connect = (TextView) findViewById(R.id.connect);
        drive = (TextView) findViewById(R.id.drive);
        calculate = (TextView) findViewById(R.id.calculate);



    }

    @Override
    protected void onResume() {
        super.onResume();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float translationY = displayMetrics.heightPixels * (-0.95f);

        if(menuIconsAnimStatus) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bg, "translationY", 0f, translationY);
            objectAnimator.setDuration(800);
            objectAnimator.setStartDelay(800);
            objectAnimator.start();
        }


        textsplash.animate().translationX(140).alpha(0).setDuration(500).setStartDelay(700);

        menuIconsAnim = new AlphaAnimation(0.0f, 1.0f);
        if(menuIconsAnimStatus) {
            menuIconsAnim.setDuration(400);
            menuIconsAnim.setStartOffset(5000);
            menuIconsAnim.setFillAfter(true);
            menuIcons.startAnimation(menuIconsAnim);
        }
        else {
            menuIconsAnim.setDuration(400);
            menuIconsAnim.setStartOffset(300);
            menuIconsAnim.setFillAfter(true);
            menuIcons.startAnimation(menuIconsAnim);
        }

        fadingTextView = findViewById(R.id.fading_text_view);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadingTextView.stop();
            }
        }, 5000);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(0,0);
                openBtActivity();
                menuIconsAnimStatus=false;

            }
        });
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(0,0);
                openDriveActivity();
                menuIconsAnimStatus=false;

            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(0,0);
                openCalcActivity();
                menuIconsAnimStatus=false;

            }
        });
    }

    public void openBtActivity() {
        Intent intent = new Intent (this, BtActivity.class);
        startActivity(intent);

    }

    public void openDriveActivity() {
        Intent intent = new Intent (this, DriveActivity.class);
        startActivity(intent);
    }

    public void openCalcActivity() {
        Intent intent = new Intent (this, CalcActivity.class);
        startActivity(intent);
}
    }
