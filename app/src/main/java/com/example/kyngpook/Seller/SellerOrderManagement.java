package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// 판매자 주문 관리 액티비티
public class SellerOrderManagement extends AppCompatActivity {

    public static final int REQUEST_CODE=200;

    private FirebaseFirestore db;
    private TextView seller_total_order_text;
    private RecyclerView recyclerView;
    private SellerOrderListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_management);

    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerView=(RecyclerView)findViewById(R.id.seller_order_management_list) ;
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter=new SellerOrderListAdapter(this);
        recyclerView.setAdapter(adapter);

        db= FirebaseFirestore.getInstance();
        seller_total_order_text=findViewById(R.id.seller_total_order_text);

        SharedPreferences pref = getSharedPreferences("seller", MODE_PRIVATE);

        final String seller_ID = pref.getString("id","");

        CollectionReference order=db.collection("주문내역");

        order.orderBy("주문시간", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot doc :task.getResult())
                        {
                            if(doc.getData().get("판매자아이디").toString().equals(seller_ID))
                            {
                                adapter.addItem(new SellerOrderListData(doc.getData().get("금액").toString(),doc.getData().get("주문상태").toString()
                                        ,doc.getData().get("구매자주소").toString(),doc.getData().get("문서이름").toString()));
                                adapter.notifyDataSetChanged();
                            }
                        }

                        seller_total_order_text.setText("총 주문 개수 : "+adapter.getItemCount());
                    }
                });
    }
}
