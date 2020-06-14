package com.example.kyngpook.Login_Signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.kyngpook.R;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignupFinishActivity extends AppCompatActivity {
    private String ID;
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_finish);
        Intent intent=getIntent();
        ID= intent.getStringExtra("ID");
        txt=(TextView)findViewById(R.id.signup_finish_txt2);
        txt.setText(ID+"님    환영합니다");
    }
    public void on_signup_finish_btn(View v){
        Intent intent=new Intent(getApplicationContext(),LogInActivity.class);
        ActivityCompat.finishAffinity(SignupFinishActivity.this);
        startActivity(intent);
        customType(SignupFinishActivity.this, "left-to-right");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupFinishActivity.this, "right-to-left");
    }
}