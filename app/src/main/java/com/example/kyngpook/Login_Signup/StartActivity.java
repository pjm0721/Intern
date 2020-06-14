package com.example.kyngpook.Login_Signup;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kyngpook.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import static maes.tech.intentanim.CustomIntent.customType;

public class StartActivity extends AppCompatActivity {
    private ImageView bike;
    private Animation anim;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        bike=(ImageView)findViewById(R.id.start_bike);
        anim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bike);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent =new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(intent);
                customType(StartActivity.this,"bottom-to-up");
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bike.startAnimation(anim);


    }

    @Override
    public void onBackPressed() {

    }
}