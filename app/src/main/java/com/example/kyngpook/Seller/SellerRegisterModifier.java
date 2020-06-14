package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// 판매자 가게글 작성 및 수정하는 액티비티
public class SellerRegisterModifier extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;

    FirebaseFirestore db;
    SellerRMListAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    EditText seller_business_name;
    EditText seller_business_master;
    ImageView seller_business_image;
    EditText seller_business_address;
    EditText seller_business_time;
    EditText seller_business_contact_number;

    EditText seller_business_explain;

    Button seller_add_thing;
    Button seller_del_thing;
    Button seller_complete;

    Spinner seller_business_category;

    String category;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register_modifier);

        seller_business_name=findViewById(R.id.seller_business_name);
        seller_business_master=findViewById(R.id.seller_business_master);
        seller_business_image=findViewById(R.id.seller_business_image);
        seller_business_address=findViewById(R.id.seller_business_address);
        seller_business_time=findViewById(R.id.seller_business_time);
        seller_business_contact_number=findViewById(R.id.seller_business_contact_number);

        seller_business_explain=findViewById(R.id.seller_business_explain);

        recyclerView=(RecyclerView)findViewById(R.id.seller_sell_list) ;
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter=new SellerRMListAdapter(this);
        recyclerView.setAdapter(adapter);


        db= FirebaseFirestore.getInstance();

        // 메뉴 리스트뷰 불러오기

        final CollectionReference things =db.collection("PRODUCT").document("대구광역시").collection("남구").document("id123").collection("판매상품");

        things
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc :task.getResult()) {
                                String price = (String) doc.getData().get("가격");
                                //DecimalFormat formatter = new DecimalFormat("###,###");
                               // String formattedPrice = formatter.format(Integer.valueOf(price)) + " 원";


                                SellerRMListData d = new SellerRMListData((String) doc.getData().get("상품이름"), (String) doc.getData().get("개수"), price);
                                adapter.addItem(d);
                                adapter.notifyDataSetChanged();
                            }

                            }
                        else
                            Log.w("sellerRM","error",task.getException());

                    }
                });


        // 스피너

        seller_business_category=findViewById(R.id.seller_business_category);

        final String[] items=new String[]{" ","가구","꽃","문구","생필품","의류","주방용품","철물"};

        ArrayAdapter<String> sp_adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);

        seller_business_category.setAdapter(sp_adapter);


        final DocumentReference docRef=db.collection("USERS").document("Seller").collection("Seller").document("id3");//임시아이디



        // 데이터 베이스에 저장된 정보 불러오기
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    document.getData().get("업소명");
                    Log.d("SellerRegisterModifier", "DocumentSnapshot data: " + document.getData());
                    seller_business_name.setText(document.getData().get("업소명").toString());
                    seller_business_master.setText(document.getData().get("대표자명").toString());
                    seller_business_address.setText(document.getData().get("주소").toString());
                    seller_business_time.setText(document.getData().get("영업시간").toString());
                    seller_business_contact_number.setText(document.getData().get("전화번호").toString());

                    category=document.getData().get("카테고리").toString();
                    for(int i=0;i<items.length;i++)
                    {
                        if(category.equals(items[i]))
                            seller_business_category.setSelection(i);
                    }
                    seller_business_explain.setText(document.getData().get("휴무일").toString());
                   //
                } else {
                    Log.d("SellerRegisterModifier", "get failed with ", task.getException());
                }
            }
        });

        //리스트 아이템 추가 버튼
        seller_add_thing=findViewById(R.id.seller_add_thing);

        seller_add_thing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent=new Intent(getApplicationContext(),SellerPlusItem.class);
              startActivityForResult(intent,REQUEST_CODE);

            }
        });




        // 체크한 리스트 아이템 삭제버튼
        seller_del_thing=findViewById(R.id.seller_del_thing);

        seller_del_thing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.checkRemoveAll();

                adapter.notifyDataSetChanged();
            }
        });



        //완료 버튼 눌렀을 때 갱신
        seller_complete=findViewById(R.id.seller_complete);

        seller_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 컬렉션 내 모든 문서 삭제
                things
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(QueryDocumentSnapshot doc :task.getResult())
                                    things.document((String)doc.getData().get("상품이름")).delete();

                            }
                        });


                // 텍스트 변경 저장
                docRef.update(
              "업소명",seller_business_name.getText().toString()
                ,
                        "대표자명",seller_business_master.getText().toString(),
                        "주소",seller_business_address.getText().toString(),
                        "영업시간",seller_business_time.getText().toString(),
                        "전화번호",seller_business_contact_number.getText().toString(),
                        "카테고리",seller_business_category.getSelectedItem().toString(),
                        "소개",seller_business_explain.getText().toString()
                );



                // 어레이 리스트에 있는거 다시 만들기

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for(SellerRMListData d : adapter.getListData())
                        {
                            Map<String,Object> item = new HashMap<>();
                            item.put("상품이름",d.name);
                            item.put("개수",d.num);
                            item.put("가격",d.price);
                            things.document(d.name).set(item);
                        }
                    }
                },500);



                Intent intent=new Intent(getApplicationContext(),SellerMainActivity.class);
                startActivity(intent);

            }
        });

    }

    // 아이템 추가 결과 받기
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                adapter.addItem(new SellerRMListData(data.getStringExtra("상품이름"),data.getStringExtra("개수"),data.getStringExtra("가격")));
                adapter.notifyDataSetChanged();
            }
        }
    }
}
