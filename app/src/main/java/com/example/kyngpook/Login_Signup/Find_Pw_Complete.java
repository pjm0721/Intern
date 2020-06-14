package com.example.kyngpook.Login_Signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kyngpook.R;

import static maes.tech.intentanim.CustomIntent.customType;

public class Find_Pw_Complete extends AppCompatActivity {

    private String PW;
    TextView pfidText;
    Button pfidButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw_complete);
        Intent intent=getIntent();
        PW = intent.getStringExtra("PW");
        pfidText=(TextView)findViewById(R.id.find_pw_complete_txt);
        pfidText.setText("비밀번호: " + PW);
        pfidButton = findViewById(R.id.find_pw_complete_btn);
        pfidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LogInActivity.class);
                ActivityCompat.finishAffinity(Find_Pw_Complete.this);
                startActivity(intent);
                customType(Find_Pw_Complete.this, "left-to-right");
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Find_Pw_Complete.this, "right-to-left");
    }
}
