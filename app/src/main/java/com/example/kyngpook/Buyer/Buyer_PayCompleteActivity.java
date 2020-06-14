package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Buyer_PayCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer__pay_complete);



        findViewById(R.id.Buyer_PayCompleteActivity_mainBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAffinity(Buyer_PayCompleteActivity.this);
                finish();
                startActivity(new Intent(getApplicationContext(), Buyer_MainActivity.class));
            }
        });


    }
}