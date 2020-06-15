package com.example.kyngpook.Login_Signup;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.kyngpook.Buyer.Buyer_MainActivity;
import com.example.kyngpook.Deliver.Deliver_MainActivity;
import com.example.kyngpook.R;
import com.example.kyngpook.Seller.SellerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import static maes.tech.intentanim.CustomIntent.customType;


public class StartActivity extends AppCompatActivity {
    private ImageView bike;
    private Animation anim;

    private LoginSharedPreferenceUtil util;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        util = new LoginSharedPreferenceUtil(this);
        final Boolean autoLogin = util.getBooleanData("AutoLogin", false);

        bike = (ImageView) findViewById(R.id.start_bike);
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_bike);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (autoLogin) {
                    String Auth = util.getStringData("권한", "null");
                    final String ID = util.getStringData("ID", "null");

                    Toast.makeText(StartActivity.this, ID + " 님 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                    if (Auth.equals("일반 이용자")) {
                        db.collection("USERS").document("Buyer").collection("Buyer")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), Buyer_MainActivity.class);
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("LoginActivity.java", document.getId() + " => " + document.getData());
                                                if (document.getId().equals(ID)) {
                                                    intent.putExtra("ID", ID);
                                                    intent.putExtra("PASSWORD", (String) document.getData().get("PASSWORD"));
                                                    intent.putExtra("닉네임", (String) document.getData().get("닉네임"));
                                                    intent.putExtra("이름", (String) document.getData().get("이름"));
                                                    intent.putExtra("전화번호", (String) document.getData().get("전화번호"));
                                                    startActivity(intent);
                                                }
                                                ;
                                            }
                                        } else {
//                             Log.w("LoginActivity.java", "Error getting documents.", task.getException());
                                        }
                                    }
                                });


                    } else if (Auth.equals("판매자")) {
                        Intent intent = new Intent(getApplicationContext(), SellerMainActivity.class);
                        intent.putExtra("ID", ID);
                        startActivity(intent);
                    } else { // 배달자
                        Intent intent = new Intent(getApplicationContext(), Deliver_MainActivity.class);
                        intent.putExtra("ID", ID);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                    startActivity(intent);
                    customType(StartActivity.this, "bottom-to-up");
                    finish();
                }
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