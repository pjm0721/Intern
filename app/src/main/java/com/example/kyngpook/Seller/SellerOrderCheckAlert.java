package com.example.kyngpook.Seller;

import com.example.kyngpook.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    FirebaseFirestore db;
    TextView seller_order_address;
    TextView seller_order_name;
    TextView seller_order_time;
    TextView seller_order_sum;
    TextView seller_order_state;

    String doc_name;

    RecyclerView recyclerView;
    SellerOrderAlertListAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Button seller_order_accept;
    Button seller_order_deny;

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

        CollectionReference order=db.collection("주문내역");

        order
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot doc :task.getResult())
                        {
                            if(doc.getData().get("판매자아이디").toString().equals("id1010") && doc.getData().get("구매자주소").toString().equals(intent.getStringExtra("구매자주소")))
                            {
                                seller_order_address.setText("구매자 주소 : "+doc.getData().get("구매자주소").toString());
                                seller_order_name.setText("구매자 이름 : "+doc.getData().get("구매자이름").toString());
                                seller_order_time.setText("주문 시간 : "+doc.getData().get("주문시간").toString());
                                seller_order_sum.setText("총 금액 : "+doc.getData().get("금액").toString());
                                seller_order_state.setText("주문상태 : "+doc.getData().get("주문상태").toString());
                                doc_name=doc.getData().get("문서이름").toString();
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

        //요청 수락 시 주문상태가 주문 수락으로 바뀐다
        seller_order_accept=findViewById(R.id.seller_order_accept);
        seller_order_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference d= db.collection("주문내역").document(doc_name);
                d.update("주문상태","주문수락");
                finish();

            }
        });

        //요청 삭제 시 주문이 삭제된다.
        seller_order_deny=findViewById(R.id.seller_order_deny);
        seller_order_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference d=db.collection("주문내역").document(doc_name);
                d.delete();
                finish();

            }
        });



    }
}
