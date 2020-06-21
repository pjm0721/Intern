package com.example.kyngpook.Seller;

import com.example.kyngpook.Buyer.Buyer_MainActivity;
import com.example.kyngpook.Deliver.Deliver_MainActivity;
import com.example.kyngpook.Login_Signup.LogInActivity;
import com.example.kyngpook.Login_Signup.LoginSharedPreferenceUtil;
import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static maes.tech.intentanim.CustomIntent.customType;


//판매자 로그인 시 처음 보이는 액티비티로, 1)내 게시글 작성 및 수정 2)내 가게 주문 내역 관리하기 두가지 버튼을 가진다
public class SellerMainActivity extends AppCompatActivity {

    private static String seller_ID;
    private long backKeyPressedTime = 0;
    private FirebaseFirestore db;
    private String si="";
    private String gu="";
    private String area;
    private LoadingDialog l;

//    protected  void onResume()
//    {
//        super.onResume();
//        l=new LoadingDialog(this);
//        l.setLoadingText("로딩중")
//                .setSuccessText("완료")
//                .setInterceptBack(true)
//                .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
//                .show();
//        Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable()  {
//            public void run() {
//                l.close();// 시간 지난 후 실행할 코딩
//            }
//        }, 1000);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);



        Intent intent=getIntent();
        seller_ID=intent.getStringExtra("ID");
        Button seller_register_modify_button=findViewById(R.id.seller_register_modify);
        Button seller_management_orders_button=findViewById(R.id.seller_management_orders);
        Button seller_management_review_button =findViewById(R.id.seller_management_review);
        db= FirebaseFirestore.getInstance();

        seller_register_modify_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),SellerRegisterModifier.class);
                intent.putExtra("si",si);
                intent.putExtra("gu",gu);
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

        //db넘기기
        final DocumentReference docRef=db.collection("USERS").document("Seller").collection("Seller").document(seller_ID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    area=task.getResult().getData().get("주소").toString();
                int c=0;
                for(int i=0;i<area.length();i++)
                {

                    if(c<2 && area.charAt(i)==' ') {
                        c++;
                    }
                    else if(c==0)
                    {
                        si+=area.charAt(i);
                    }
                    else if(c==1)
                    {
                        gu+=area.charAt(i);
                    }
                    else
                    {
                        break;
                    }
                }

            }
        }});

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
