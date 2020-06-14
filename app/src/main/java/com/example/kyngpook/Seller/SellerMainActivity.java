package com.example.kyngpook.Seller;

import com.example.kyngpook.R;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


//판매자 로그인 시 처음 보이는 액티비티로, 1)내 게시글 작성 및 수정 2)내 가게 주문 내역 관리하기 두가지 버튼을 가진다
public class SellerMainActivity extends AppCompatActivity {

    private long backKeyPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

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
            finish();
            toast.cancel();
        }
    }



}
