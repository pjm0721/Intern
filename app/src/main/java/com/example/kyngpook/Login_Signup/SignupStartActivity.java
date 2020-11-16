package com.example.kyngpook.Login_Signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.kyngpook.R;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignupStartActivity extends AppCompatActivity {
    private ImageView signup_buyer;
    private ImageView signup_seller;
    private ImageView signup_deliver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_start);

        signup_buyer=(ImageView)findViewById(R.id.signup_buyerbtn);
        signup_seller=(ImageView)findViewById(R.id.signup_sellerbtn);
        signup_deliver=(ImageView)findViewById(R.id.signup_deliverbtn);
    }
    public void on_signup_start_buyer(View v){
        Intent intent=new Intent(getApplicationContext(),SignupBuyerActivity1.class);
        startActivity(intent);
        customType(this, "left-to-right");
    }
    public void on_signup_start_seller(View v){
        Intent intent=new Intent(getApplicationContext(),SignupSellerActivity.class);
        startActivity(intent);
        customType(this, "left-to-right");
    }
    public void on_signup_start_deliver(View v){
        Intent intent=new Intent(getApplicationContext(),SignupDeliverActivity.class);
        startActivity(intent);
        customType(this, "left-to-right");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupStartActivity.this, "right-to-left");
    }
}
