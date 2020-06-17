package com.example.kyngpook.Buyer;

import com.example.kyngpook.Login_Signup.Find_Id_Complete;
import com.example.kyngpook.Login_Signup.LogInActivity;
import com.example.kyngpook.Login_Signup.LoginSharedPreferenceUtil;
import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;


public class Buyer_MainActivity extends AppCompatActivity {
    private SharedPreferenceUtil util;
    private Buyer_MarketList_Fragment fragment1;
    private Buyer_OrderList_Fragment fragment2;
    private Buyer_ModifyInfo_Fragment fragment3;
    private long backKeyPressedTime = 0;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ImageView order_img_v;
    private ImageView history_img_v;
    private ImageView modify_img_v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_main);

        Intent intent = getIntent();
        util = new SharedPreferenceUtil(this);

//        util.setStringData("PASSWORD", intent.getStringExtra("PASSWORD"));
//        util.setStringData("ID", intent.getStringExtra("ID"));
//        util.setStringData("전화번호", intent.getStringExtra("전화번호"));
//        util.setStringData("닉네임", intent.getStringExtra("닉네임"));
//        util.setStringData("이름", intent.getStringExtra("이름"));

        order_img_v=(ImageView)findViewById(R.id.Buyer_MainActivity_OrderImageView);
        modify_img_v=(ImageView)findViewById(R.id.Buyer_MainActivity_ModifyImageView);
        history_img_v=(ImageView)findViewById(R.id.Buyer_MainActivity_HistoryImageView);
        fragment1 = new Buyer_MarketList_Fragment();
        fragment2 = new Buyer_OrderList_Fragment();
        fragment3 = new Buyer_ModifyInfo_Fragment();

        fragmentManager.beginTransaction().add(R.id.Buyer_MainActivity_Frame, fragment1).replace(R.id.Buyer_MainActivity_Frame, fragment1).commit();
        fragmentManager.beginTransaction().add(R.id.Buyer_MainActivity_Frame, fragment2).hide(fragment2).commit();
        fragmentManager.beginTransaction().add(R.id.Buyer_MainActivity_Frame, fragment3).hide(fragment3).commit();


        findViewById(R.id.Buyer_MainActivity_OrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragProcessing(fragment1);
                fragmentManager.beginTransaction().show(fragment1).commit();
                order_img_v.setImageResource(R.drawable.buyer_order_btn);
                history_img_v.setImageResource(R.drawable.buyer_history_btn2);
                modify_img_v.setImageResource(R.drawable.buyer_modify_btn2);
            }
        });
        //
        findViewById(R.id.Buyer_MainActivity_OrderListBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragProcessing(fragment2);
                fragmentManager.beginTransaction().show(fragment2).commit();
                order_img_v.setImageResource(R.drawable.buyer_order_btn2);
                history_img_v.setImageResource(R.drawable.buyer_history_btn);
                modify_img_v.setImageResource(R.drawable.buyer_modify_btn2);
            }
        });

        //회원 정보 수정
        findViewById(R.id.Buyer_MainActivity_ModifyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragProcessing(fragment3);
                fragmentManager.beginTransaction().show(fragment3).commit();
                order_img_v.setImageResource(R.drawable.buyer_order_btn2);
                history_img_v.setImageResource(R.drawable.buyer_history_btn2);
                modify_img_v.setImageResource(R.drawable.buyer_modify_btn);
            }
        });

    }

    public void startAddressRegistActivity() {
        Intent intent = new Intent(Buyer_MainActivity.this, Buyer_AddressRegistActivity.class);
        startActivityForResult(intent, 100);
    }

                @Override
                protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                    super.onActivityResult(requestCode, resultCode, data);
                    if (requestCode == 100) {
                        if (data != null && resultCode == RESULT_OK) {
                            final String address1 = data.getStringExtra("주소1");
                            final String address2 = data.getStringExtra("주소2");
                String address3 = data.getStringExtra("주소3");

                fragment1.getResultFromActivity(address1, address2, address3);

            } else {
//                Log.d("Buyer_MarketListActivity", "onActivityResult Error");
            }
        }
    }

    public void addReviewDb1(Map<String, Object> rv, orderInfo info) {
        fragment2.addReviewDb(rv, info);
    }

    //Fragment Processing
    public void FragProcessing(Fragment id) {
        if (fragment1 != id)
            fragmentManager.beginTransaction().hide(fragment1).commit();
        if (fragment2 != id)
            fragmentManager.beginTransaction().hide(fragment2).commit();
        if (fragment3 != id)
            fragmentManager.beginTransaction().hide(fragment3).commit();
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
            //Boolean goToLogin = util11.getBooleanData("AutoLogin", false);
//            if(goToLogin) {
            ActivityCompat.finishAffinity(Buyer_MainActivity.this);
//            }
            finish();
            toast.cancel();
        }
    }
    public void Buyer_Logout() {
        LoginSharedPreferenceUtil util11 =  new LoginSharedPreferenceUtil(Buyer_MainActivity.this);
        util11.setBooleanData("AutoLogin", false);
        util11.setStringData("ID", "");
        util11.setStringData("권한", "null");
        Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Buyer_MainActivity.this,LogInActivity.class);
        ActivityCompat.finishAffinity(Buyer_MainActivity.this);
        startActivity(intent);
        customType(Buyer_MainActivity.this, "bottom-to-up");
    }
}

class Buyer_Seller {
    String ID, RepresentativeName;
    String StoreName, storeAddress;
    String img;
    String OnTime;
    String Holiday;
    String Address1, Address2, CallNum;
    int ReviewNum;

    boolean checked = false;

    public Buyer_Seller(String ID, String StoreName, String storeAddress, String img, String Ontime, String Holiday, String a1, String a2,
                        String RepresentativeName, String CallNum, int Review) {
        this.ID = ID;
        this.Holiday = Holiday;
        this.img = img;
        this.StoreName = StoreName;
        this.storeAddress = storeAddress;
        this.OnTime = Ontime;
        this.Address1 = a1;
        this.Address2 = a2;
        this.RepresentativeName = RepresentativeName;
        this.CallNum = CallNum;
        this.ReviewNum = Review;
    }
}
class orderInfo {
    String store, time, state, address, order;
    int price;
    boolean review;
    String sellerID, DB;

    public orderInfo(String store, String time, String state, String address, String order, int price, boolean review, String sellerid, String DB) {
        this.store = store;
        this.time = time;
        this.state = state;
        this.address = address;
        this.order = order;
        this.price = price;
        this.review = review;
        this.sellerID = sellerid;
        this.DB = DB;
    }

    @NonNull
    @Override
    public String toString() {
        return this.store + " " + this.time + " " + this.review + " " + sellerID;
    }

}