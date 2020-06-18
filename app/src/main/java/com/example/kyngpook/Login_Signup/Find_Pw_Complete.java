package com.example.kyngpook.Login_Signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyngpook.R;
import com.google.firebase.firestore.FirebaseFirestore;

import static maes.tech.intentanim.CustomIntent.customType;

public class Find_Pw_Complete extends AppCompatActivity {

    private String ID;
    private String User;
    private EditText fpw;
    private EditText fpwc;
    private Button pfidButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw_complete);
        Intent intent=getIntent();
        ID = intent.getStringExtra("ID");
        User = intent.getStringExtra("USER");
        Toast.makeText(getApplicationContext(),ID,Toast.LENGTH_SHORT);
        Toast.makeText(getApplicationContext(),User,Toast.LENGTH_SHORT);
        fpw = findViewById(R.id.find_pw_password);
        fpwc = findViewById(R.id.find_pw_pCheck);
        pfidButton = findViewById(R.id.find_pw_complete_btn);
        pfidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkPw();
            }
        });
    }
    private void checkPw()
    {
        String findpw = fpw.getText().toString();
        String findpwc = fpwc.getText().toString();
        if(findpw.length()<8||findpw.length()>12)
            Toast.makeText(getApplicationContext(), "비밀번호는 8자 이상 12자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
        else if(!findpw.equals(findpwc))
            Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
        else {
            db.collection("USERS").document(User).collection(User).document(ID).update("PASSWORD",findpw);
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            ActivityCompat.finishAffinity(Find_Pw_Complete.this);
            startActivity(intent);
            customType(Find_Pw_Complete.this, "left-to-right");
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Find_Pw_Complete.this, "right-to-left");
    }
}
