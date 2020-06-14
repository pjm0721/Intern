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

public class Find_Id_Complete extends AppCompatActivity {

    private String ID;
    TextView fidText;
    Button fidButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_complete);
        Intent intent=getIntent();
        ID = intent.getStringExtra("ID");
        fidText=(TextView)findViewById(R.id.find_id_complete_txt);
        fidText.setText("아이디: " + ID);
        fidButton = findViewById(R.id.find_id_complete_btn);
        fidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LogInActivity.class);
                ActivityCompat.finishAffinity(Find_Id_Complete.this);
                startActivity(intent);
                customType(Find_Id_Complete.this, "left-to-right");
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Find_Id_Complete.this, "right-to-left");
    }
}
