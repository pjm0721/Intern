package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Buyer_ReviewActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    private Buyer_Review_Adapter RVadapter1;

    private double scoresum = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer__review);

        Intent intent = getIntent();
        final String ID = intent.getStringExtra("ID");
        final String name = intent.getStringExtra("대표자명");
        final String storename = intent.getStringExtra("업소명");
        final String call = intent.getStringExtra("전화번호");
        final String time = intent.getStringExtra("영업시간");
        final int review = intent.getIntExtra("리뷰", 0);

        TextView nameText = (TextView) findViewById(R.id.Buyer_ReviewActivity_PresentativeText);
        nameText.setText("대표자명 : " + name);
        TextView storenameText = (TextView) findViewById(R.id.Buyer_ReviewActivity_StoreNameText);
        storenameText.setText(storename);
        TextView callText = (TextView) findViewById(R.id.Buyer_ReviewActivity_callText);
        callText.setText("☎ : " + call);
        TextView timeText = (TextView) findViewById(R.id.Buyer_ReviewActivity_timeText);
        timeText.setText(time);
        final TextView reviewText = (TextView) findViewById(R.id.Buyer_ReviewActivity_numText);


        final TextView scoreText = (TextView) findViewById(R.id.Buyer_ReviewActivity_scoreText);

        final ImageView imageView = (ImageView) findViewById(R.id.Buyer_ReviewActivity_ImageView);
        storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + ID + "/" + ID + ".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(getApplicationContext())
                            .load(task.getResult())
                            .into(imageView);
                } else {
                    Log.d("Buyer_MarketList_Adapter", "Glide Error");
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.Buyer_ReviewActivity_RecyclerView1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        RVadapter1 = new Buyer_Review_Adapter();
        recyclerView.setAdapter(RVadapter1);

        db.collection("USERS").document("Seller").collection("Seller")
                .document(ID).collection("리뷰")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int reviewNum = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String time = (String) document.getData().get("시간");
                                String nickname = (String) document.getData().get("닉네임");
                                String content = (String) document.getData().get("내용");
                                int score = Integer.valueOf(document.getData().get("평점").toString());
                                Buyer_Review br = new Buyer_Review(time, nickname, content, score);

                                //DB에서 받아서 item 추가하기
                                RVadapter1.addItem(br);
                                RVadapter1.notifyDataSetChanged();
                                scoresum += score;

                                reviewNum++;
                            }
                            if(reviewNum > 0) {
                                reviewText.setText("최근 리뷰   " + reviewNum + " 개");
                                scoreText.setText("평점 평균 : " + Math.round(scoresum*10)/reviewNum/10.0 + " / 5.0");
                            }
                        }
                        else {
                            Log.w("Buyer_MarketListActiviry", "Error getting documents.", task.getException());
                        }
                    }
                });

    }
    class Buyer_Review {
        String time;
        String nickname;
        String content;
        int score;
        public Buyer_Review(String time, String nickname, String content, int score){
            this.content = content; this.nickname = nickname; this.time = time; this.score = score;
        }

    }
}
