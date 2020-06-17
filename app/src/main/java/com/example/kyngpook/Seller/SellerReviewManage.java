package com.example.kyngpook.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class SellerReviewManage extends AppCompatActivity {
    private ListView seller_review_manage_boardlistview = null;
    private SellerReviewAdapter seller_review_manage_adapter = null;
    private List<SellerReviewItem> seller_review_manage_boardlist = null;
    private TextView seller_review_manage_num = null;

    private FirebaseFirestore db;
    private String ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_review_manage);

        seller_review_manage_num = (TextView)findViewById(R.id.seller_review_manage_total_num);
        seller_review_manage_boardlistview = (ListView) findViewById(R.id.seller_review_manage_listview);
        seller_review_manage_boardlist = new ArrayList<SellerReviewItem>();
        seller_review_manage_adapter = new SellerReviewAdapter(getApplicationContext(), seller_review_manage_boardlist);
        seller_review_manage_boardlistview.setAdapter(seller_review_manage_adapter);
        ID = getIntent().getExtras().getString("SELLER_ID");
        seller_review_manage_getdata();
    }

    private void seller_review_manage_getdata() {
        db = FirebaseFirestore.getInstance();

        db.collection("USERS")
                .document("Seller")
                .collection("Seller")
                .document(ID)
                .collection("리뷰")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            seller_review_manage_boardlist.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String document_content = doc.getData().get("내용").toString();
                                String document_nickname = doc.getData().get("닉네임").toString();
                                String document_time = doc.getData().get("시간").toString();
                                String document_review_point = doc.getData().get("평점").toString();

                                seller_review_manage_boardlist.add(new SellerReviewItem(document_content,
                                        document_nickname, document_time, Integer.parseInt(document_review_point)));
                            }
                        }

                        seller_review_manage_num.setText(Integer.toString(seller_review_manage_boardlist.size()));
                        seller_review_manage_adapter.notifyDataSetChanged();
                    }
                });
    }
}