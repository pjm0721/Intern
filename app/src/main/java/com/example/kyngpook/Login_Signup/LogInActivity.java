package com.example.kyngpook.Login_Signup;
//github

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyngpook.Buyer.Buyer_MainActivity;
import com.example.kyngpook.Buyer.SharedPreferenceUtil;
import com.example.kyngpook.Deliver.Deliver_MainActivity;
import com.example.kyngpook.R;
import com.example.kyngpook.Seller.SellerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static maes.tech.intentanim.CustomIntent.customType;

public class LogInActivity extends AppCompatActivity {
    private TextView id;
    private TextView password;
    private RadioGroup login_grp;
    private Toast toast;
    private long backKeyPressedTime = 0;
    private Button findId;
    private Button findPw;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    CheckBox autoCheckBox;
    private LoginSharedPreferenceUtil util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (TextView) findViewById(R.id.login_id);
        password = (TextView) findViewById(R.id.login_password);
        login_grp = (RadioGroup) findViewById(R.id.login_rgp);
        findId = findViewById(R.id.login_find_id);
        findPw = findViewById(R.id.login_find_password);

        util = new LoginSharedPreferenceUtil(this);
        util.setBooleanData("AutoLogin", false);
        util.setStringData("ID", "");
        util.setStringData("권한", "null");

        autoCheckBox = (CheckBox) findViewById(R.id.login_autoCheckBox);

        findId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Find_Id_Activity.class);
                startActivity(intent);
                customType(LogInActivity.this, "left-to-right");
            }
        });
        findPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Find_Pw_Activity.class);
                startActivity(intent);
                customType(LogInActivity.this, "left-to-right");
            }
        });
    }

    private int scs = 0;

    public void on_login(View v) {
        final String idtext = id.getText().toString();
        final String pwtext = password.getText().toString();
        if (idtext.isEmpty() == true || pwtext.isEmpty() == true) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_LONG).show();
            login_init();
            return;
        }
        int id = login_grp.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(id);
        final String who = rb.getText().toString();
        String user = null;
        String ID = null;
        if (who.equals("일반 이용자") == true) user = "Buyer";
        else if (who.equals("판매자") == true) user = "Seller";
        else if (who.equals("배달원") == true) user = "Deliver";
        db.collection("USERS").document(user).collection(user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LoginActivity.java", document.getId() + " => " + document.getData());
                                if (document.getId().equals(idtext) == true) {
                                    if (document.getData().get("PASSWORD").toString().equals(pwtext) == true) {
                                        login_success(who, idtext, pwtext, (String) document.getData().get("닉네임"), (String) document.getData().get("이름"), (String) document.getData().get("전화번호"));
                                    }
                                }
                                ;
                            }
                            if (scs == 0) login_fail();
                            scs = 0;
                        } else {
//                             Log.w("LoginActivity.java", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void on_login_signup(View v) {
        Intent intent = new Intent(getApplicationContext(), SignupStartActivity.class);
        startActivity(intent);
        customType(this, "left-to-right");
    }

    private void login_success(String who, String ID, String PASSWORD, String NICK, String NAME, String PHONE) {
        scs = 1;
        Boolean autoLogin = autoCheckBox.isChecked();

        if (who.equals("일반 이용자") == true) {
            toast = Toast.makeText(this, ID + " 님 로그인에 성공하였습니다.", Toast.LENGTH_SHORT);
            Intent intent = new Intent(getApplicationContext(), Buyer_MainActivity.class);
            intent.putExtra("ID", ID);
            intent.putExtra("PASSWORD", PASSWORD);
            intent.putExtra("닉네임", NICK);
            intent.putExtra("이름", NAME);
            intent.putExtra("전화번호", PHONE);

            SharedPreferenceUtil Buyer_util = new SharedPreferenceUtil(LogInActivity.this);
            Buyer_util.setStringData("PASSWORD", intent.getStringExtra("PASSWORD"));
            Buyer_util.setStringData("ID", intent.getStringExtra("ID"));
            Buyer_util.setStringData("전화번호", intent.getStringExtra("전화번호"));
            Buyer_util.setStringData("닉네임", intent.getStringExtra("닉네임"));
            Buyer_util.setStringData("이름", intent.getStringExtra("이름"));

            startActivity(intent);
            finish();
            if (autoLogin) {
                util.setBooleanData("AutoLogin", true);
                util.setStringData("ID", ID);
                util.setStringData("권한", "일반 이용자");
            }
            else {
                util.setBooleanData("AutoLogin", false);
                util.setStringData("ID", "null");
                util.setStringData("권한", "일반 이용자");
            }
        } else if (who.equals("판매자") == true) {
            toast = Toast.makeText(this, ID + " 님 로그인에 성공하였습니다.", Toast.LENGTH_SHORT);
            Intent intent = new Intent(getApplicationContext(), SellerMainActivity.class);
            intent.putExtra("ID", ID);
            startActivity(intent);
            finish();
            if (autoLogin) {
                util.setBooleanData("AutoLogin", true);
                util.setStringData("ID", ID);
                util.setStringData("권한", "판매자");
            }
            else {
                util.setBooleanData("AutoLogin", false);
                util.setStringData("ID", "null");
                util.setStringData("권한", "판매자");
            }
        } else if (who.equals("배달원") == true) {
            toast = Toast.makeText(this, ID + " 님 로그인에 성공하였습니다.", Toast.LENGTH_SHORT);
            Intent intent = new Intent(getApplicationContext(), Deliver_MainActivity.class);
            intent.putExtra("ID", ID);
            startActivity(intent);
            finish();
            if (autoLogin) {
                util.setBooleanData("AutoLogin", true);
                util.setStringData("ID", ID);
                util.setStringData("권한", "배달원");
            }
            else {
                util.setBooleanData("AutoLogin", false);
                util.setStringData("ID", "null");
                util.setStringData("권한", "배달원");
            }
        } else toast = Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 1000);
        toast.show();
        login_init();
    }

    private void login_fail() {
        toast = Toast.makeText(this, "아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 1000);
        toast.show();
        login_init();
    }

    private void login_init() {
        id.setText(null);
        password.setText(null);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "종료 하시겠습니까?", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }
}