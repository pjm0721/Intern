package com.example.kyngpook.Seller;

import com.example.kyngpook.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.Map;

//판매자 주문내역 관리 상세보기 액티비티
public class SellerOrderCheckAlert extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView seller_order_address;
    private TextView seller_order_name;
    private TextView seller_order_time;
    private TextView seller_order_sum;
    private TextView seller_order_state;

    private String deliver;

    private String doc_name;
    private String order_state;

    private String buyer_id;
    private String buyer_num;

    private RecyclerView recyclerView;
    private SellerOrderAlertListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Button seller_order_accept;
    private Button seller_order_deny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_check_alert);

        recyclerView=(RecyclerView)findViewById(R.id.seller_order_list) ;
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter=new SellerOrderAlertListAdapter(this);
        recyclerView.setAdapter(adapter);

        final Intent intent=getIntent();

        db= FirebaseFirestore.getInstance();

        seller_order_address=findViewById(R.id.seller_order_address);
        seller_order_name=findViewById(R.id.seller_order_name);
        seller_order_time=findViewById(R.id.seller_order_time);
        seller_order_sum=findViewById(R.id.seller_order_sum);
        seller_order_state=findViewById(R.id.seller_order_state);

        SharedPreferences pref = getSharedPreferences("seller", MODE_PRIVATE);

        final String seller_ID = pref.getString("id","");

        CollectionReference order=db.collection("주문내역");

        order
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot doc :task.getResult())
                        {
                            if(doc.getData().get("판매자아이디").toString().equals(seller_ID) && doc.getData().get("문서이름").toString().equals(intent.getStringExtra("문서이름")))
                            {
                                seller_order_address.setText("구매자 주소 : "+doc.getData().get("구매자주소").toString());
                                seller_order_name.setText("구매자 이름 : "+doc.getData().get("구매자이름").toString());
                                seller_order_time.setText("주문 시간 : "+doc.getData().get("주문시간").toString());
                                seller_order_sum.setText("총 금액 : "+doc.getData().get("금액").toString()+" 원");
                                seller_order_state.setText("주문 상태 : "+doc.getData().get("주문상태").toString());
                                order_state=doc.getData().get("주문상태").toString();
                                doc_name=doc.getData().get("문서이름").toString();
                                buyer_id=doc.getData().get("구매자아이디").toString();
                                deliver = doc.getData().get("배달자담당아이디").toString();
                                Map orderMap =(Map)doc.getData().get("주문내역");

                                for(Object keyy : orderMap.keySet())
                                {
                                    String key=(String)keyy;
                                    adapter.addItem(new SellerOALData(key,orderMap.get(key).toString()));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }
                });


        db.collection("USERS").document("Buyer").collection("Buyer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String temp_seller_id = document.get("ID").toString();
                                String temp_phone_number = document.get("전화번호").toString();

                                if (temp_seller_id.equals(buyer_id)) {
                                    buyer_num = temp_phone_number;
                                }
                            }
                        }
                    }
                });



        //요청 수락 시 주문상태가 주문 수락으로 바뀐다
        seller_order_accept=findViewById(R.id.seller_order_accept);
        seller_order_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference d= db.collection("주문내역").document(doc_name);

                if(order_state.equals("주문대기")) {
                    send_sms(buyer_num,"<크누마켓>\n주문이 수락되었습니다.");
                    d.update("주문상태", "주문수락");
                }
                else{
                    Toast.makeText(getApplicationContext(),"이미 완료된 주문입니다.",Toast.LENGTH_LONG).show();
                }
                finish();

            }
        });

        //요청 삭제 시 주문이 삭제된다.
        seller_order_deny=findViewById(R.id.seller_order_deny);
        seller_order_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (order_state.equals("주문수락") && !deliver.equals("")) {
                    Toast.makeText(getApplicationContext(), "현재 배송 중입니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    if (order_state.equals("주문대기")||order_state.equals("주문수락"))
                        send_sms(buyer_num, "<크누마켓> 주문이 취소되었습니다.");
                    DocumentReference d = db.collection("주문내역").document(doc_name);
                    d.delete();
                    finish();
                }
            }
        });


    }

    private void send_sms(final String number, final String message) {
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, message,
                    PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0),
                    PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
