package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// 판매자 가게 정보 수정 시 메뉴 추가 시 Alert형태로 뜨는 액티비티
public class SellerPlusItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_plus_item);

        final TextView seller_add_name = findViewById(R.id.seller_add_name);
        final TextView seller_add_num =findViewById(R.id.seller_add_num);
        final TextView seller_add_price=findViewById(R.id.seller_add_price);

        Button seller_add_ok=findViewById(R.id.seller_add_ok);
        Button seller_add_cancle=findViewById(R.id.seller_add_cancle);

        // 확인 버튼 누르면 입력 데이터를 넘겨준다
        seller_add_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                String name=seller_add_name.getText().toString();
                String num=seller_add_num.getText().toString();
                String price=seller_add_price.getText().toString();

                if(name.length()!=0 && num.length()!=0 && price.length()!=0) {
                    intent.putExtra("상품이름", name);
                    intent.putExtra("개수", num);
                    intent.putExtra("가격", price);
                    setResult(RESULT_OK, intent);
                }else {
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(getApplicationContext(),"모두 입력해주세요",Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        // 취소 버튼 누르면 그냥 닫긴다
        seller_add_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
    }
}
