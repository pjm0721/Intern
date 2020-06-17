package com.example.kyngpook.Deliver;

import com.example.kyngpook.Login_Signup.LogInActivity;
import com.example.kyngpook.Login_Signup.StartActivity;
import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class Deliver_Item_Information extends AppCompatActivity {
    private Button deliver_item_information_complete_button;
    private Button deliver_item_information_call_button;

    private ListView deliver_item_information_listview;
    private ArrayAdapter deliver_item_information_adapter = null;
    private ArrayList<String> deliver_item_information_arraylist = null;
    private ImageView bike;
    private Animation anim;
    private final int RESULT_ACCEPT = 1001;
    private FirebaseFirestore db;
    private String temp_document_name = "";
    private String buyer_phone_number = "";

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "배달완료 후 완료버튼을 누르셔야합니다.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_item_information);

        db = FirebaseFirestore.getInstance();
        temp_document_name = getIntent().getExtras().getString("DOCUMENT_NAME", "");

        bike = (ImageView) findViewById(R.id.Deliver_item_bike);
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_bike_2);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        bike.startAnimation(anim);
        deliver_item_infomation_get_buyer_phone(getIntent().getExtras().getString("BUYER_ID"));

        deliver_item_information_arraylist = new ArrayList<String>();
        deliver_item_information_arraylist.add("주문자 이름 : " + getIntent().getExtras().getString("SELLER_NAME"));
        deliver_item_information_arraylist.add("주문자 주소 : " + getIntent().getExtras().getString("SELLER_ADDRESS"));
        deliver_item_information_arraylist.add("구매자 이름 : " + getIntent().getExtras().getString("BUYER_NAME"));
        deliver_item_information_arraylist.add("구매자 주소 : " + getIntent().getExtras().getString("BUYER_ADDRESS"));

        DecimalFormat formatter = new DecimalFormat("###,###");
        String priceText = formatter.format(getIntent().getExtras().getInt("PRICE"));

        deliver_item_information_arraylist.add("총 결제금액 : " + priceText + " 원");
        deliver_item_information_arraylist.add("주문요청시간 : " + getIntent().getExtras().getString("ORDER_TIME"));
        deliver_item_information_adapter = new ArrayAdapter(Deliver_Item_Information.this, android.R.layout.simple_list_item_1, deliver_item_information_arraylist);
        deliver_item_information_listview = (ListView) findViewById(R.id.adii_listview);
        deliver_item_information_listview.setAdapter(deliver_item_information_adapter);

        deliver_item_information_complete_button = (Button) findViewById(R.id.adii_complete_button);
        deliver_item_information_complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference dr = db.collection("주문내역").document(temp_document_name);
                dr.update("배달현황", true);
                dr.update("주문상태", "주문완료");
                setResult(RESULT_ACCEPT, new Intent());
                finish();
            }
        });

        deliver_item_information_call_button = (Button) findViewById(R.id.adii_call_button);
        deliver_item_information_call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + buyer_phone_number)));
            }
        });

    }

    private void deliver_item_infomation_get_buyer_phone(final String buyer_id) {
        buyer_phone_number = "";
        db = FirebaseFirestore.getInstance();

        db.collection("USERS").document("Buyer").collection("Buyer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String temp_buyer_id = doc.get("ID").toString();
                                String temp_phone_number = doc.get("전화번호").toString();

                                if (temp_buyer_id.equals(buyer_id)) {
                                    buyer_phone_number += temp_phone_number;
                                }
                            }
                        }
                    }
                });
    }
}