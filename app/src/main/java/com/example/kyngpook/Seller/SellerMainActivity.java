package com.example.kyngpook.Seller;

import com.example.kyngpook.Deliver.Deliver_MainActivity;
import com.example.kyngpook.Login_Signup.LogInActivity;
import com.example.kyngpook.Login_Signup.LoginSharedPreferenceUtil;
import com.example.kyngpook.R;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


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


        SharedPreferences pref=getSharedPreferences("seller",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Intent intent2=getIntent();
        editor.putString("id",intent2.getStringExtra("ID"));
        editor.commit();
    }
    @Override
    public void onBackPressed() {
        Toast toast;
        toast= Toast.makeText(this, "초기화", Toast.LENGTH_SHORT);
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "한번 더 누르면 로그아웃됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            LoginSharedPreferenceUtil util11 =  new LoginSharedPreferenceUtil(this);
            //Boolean goToLogin = util11.getBooleanData("AutoLogin", false);

            util11.setBooleanData("AutoLogin", false);
            util11.setStringData("ID", "");
            util11.setStringData("권한", "null");
//            if(goToLogin) {
               startActivity(new Intent(SellerMainActivity.this, LogInActivity.class));
//            }
            finish();
            toast.cancel();
        }
    }


}
