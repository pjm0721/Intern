package com.example.kyngpook.Seller;

import com.example.kyngpook.Buyer.Buyer_MainActivity;
import com.example.kyngpook.Deliver.Deliver_MainActivity;
import com.example.kyngpook.Login_Signup.LogInActivity;
import com.example.kyngpook.Login_Signup.LoginSharedPreferenceUtil;
import com.example.kyngpook.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static maes.tech.intentanim.CustomIntent.customType;


//판매자 로그인 시 처음 보이는 액티비티로, 1)내 게시글 작성 및 수정 2)내 가게 주문 내역 관리하기 두가지 버튼을 가진다
public class SellerMainActivity extends AppCompatActivity {

    private static String seller_ID;
    private long backKeyPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);
        Intent intent=getIntent();
        seller_ID=intent.getStringExtra("ID");
        Button seller_register_modify_button=findViewById(R.id.seller_register_modify);
        Button seller_management_orders_button=findViewById(R.id.seller_management_orders);
        Button seller_management_review_button =findViewById(R.id.seller_management_review);

        seller_register_modify_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),SellerRegisterModifier.class);
                startActivity(intent);
            }
        } );

        seller_management_orders_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),SellerOrderManagement.class);
                startActivity(intent);
            }
        });

        seller_management_review_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SellerReviewManage.class);
                intent.putExtra("SELLER_ID", seller_ID);
                startActivity(intent);
            }
        });


        SharedPreferences pref=getSharedPreferences("seller",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Intent intent2=getIntent();
        editor.putString("id",intent2.getStringExtra("ID"));
        editor.commit();
    }
    public void on_seller_logout(View v){
        LoginSharedPreferenceUtil util11 =  new LoginSharedPreferenceUtil(SellerMainActivity.this);
        util11.setBooleanData("AutoLogin", false);
        util11.setStringData("ID", "");
        util11.setStringData("권한", "null");
        Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(SellerMainActivity.this,LogInActivity.class);
        ActivityCompat.finishAffinity(SellerMainActivity.this);
        startActivity(intent);
        customType(SellerMainActivity.this, "bottom-to-up");
    }
    @Override
    public void onBackPressed() {
        Toast toast;
        toast= Toast.makeText(this, "초기화", Toast.LENGTH_SHORT);
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "종료 하시겠습니까?", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            LoginSharedPreferenceUtil util11 =  new LoginSharedPreferenceUtil(this);
            //Boolean goToLogin = util11.getBooleanData("AutoLogin", false);
//            if(goToLogin) {
            ActivityCompat.finishAffinity(SellerMainActivity.this);
//            }
            finish();
            toast.cancel();
        }
    }


}
