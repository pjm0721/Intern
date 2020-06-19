package com.example.kyngpook.Seller;

import com.bumptech.glide.Glide;
import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.opencensus.common.ServerStatsFieldEnums;

import static android.os.Build.ID;

// 판매자 가게글 작성 및 수정하는 액티비티
public class SellerRegisterModifier extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST=1000;
    private LoadingDialog l;
    private FirebaseFirestore db;
    private SellerRMListAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private EditText seller_business_name;
    private EditText seller_business_master;
    private ImageView seller_business_image;
    private TextView seller_business_address;
    private EditText seller_business_time;
    private EditText seller_business_contact_number;

    private EditText seller_business_explain;

    private Button seller_add_thing;
    private Button seller_del_thing;
    private Button seller_complete;

    private Spinner seller_business_category;
    private LoadingDialog l2;
    private String category;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Bitmap bitmap;

    private String si,gu;
    private CollectionReference things;

    private String seller_ID;

    private ArrayList<SellerRMListData> listData=new ArrayList<SellerRMListData>();


    @Override
    protected void onStart() {
        super.onStart();

        // 메뉴 리스트뷰 불러오기

        things = db.collection("PRODUCT").document(si).collection(gu).document(seller_ID).collection("판매상품");

        things.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(queryDocumentSnapshots != null){
                    listData.clear();
                    for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){

                        listData.add(new SellerRMListData(String.valueOf(doc.getData().get("상품이름")),
                                (String) doc.getData().get("개수"), (String) doc.getData().get("가격")));
                    }

                    adapter=new SellerRMListAdapter(getApplicationContext(),listData);

                }
                recyclerView.setAdapter(adapter);

            }});

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("seller", MODE_PRIVATE);
        seller_ID = pref.getString("id","");

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


        recyclerView.setAdapter(adapter);
        si=getIntent().getStringExtra("si");
        gu=getIntent().getStringExtra("gu");
        bitmap=null;
        l=new LoadingDialog(this);
        l.setLoadingText("로딩중")
                .setSuccessText("완료")
                .setInterceptBack(true)
                .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                .show();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                l.close();// 시간 지난 후 실행할 코딩
            }
        }, 1700);



        db= FirebaseFirestore.getInstance();


        // 카테고리 스피너

        seller_business_category=findViewById(R.id.seller_business_category);

        final String[] items=new String[]{" ","가구","꽃","문구","생필품","의류","주방용품","철물","음식","기타"};

        ArrayAdapter<String> sp_adapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, items);

        seller_business_category.setAdapter(sp_adapter);



        // 이미지
        storage = FirebaseStorage.getInstance();

        seller_business_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,IMAGE_REQUEST);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()  {
            public void run() {
                storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + seller_ID + "/" + seller_ID + ".jpg");
                storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Glide.with(getApplicationContext())
                                    .load(task.getResult())
                                    .into(seller_business_image);
                        }
                        else
                        {
                            Log.d("SellerRM", "Glide Error");
                        }
                    }
                });
            }
        }, 300);



        final DocumentReference docRef=db.collection("USERS").document("Seller").collection("Seller").document(seller_ID);

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

                storage = FirebaseStorage.getInstance();
                for(int i=0;i<listData.size();i++)
                {
                    if(listData.get(i).check==true)
                    {
                        storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + seller_ID + "/" + listData.get(i).name + ".jpg");
                        storageRef.delete();
                        things.document(listData.get(i).name).delete();
                    }
                }

            }
        });



        //완료 버튼 눌렀을 때 갱신
        seller_complete=findViewById(R.id.seller_complete);

        seller_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 텍스트 변경 저장
                docRef.update(
                        "업소명",seller_business_name.getText().toString()
                        ,
                        "대표자명",seller_business_master.getText().toString(),
                        "주소", seller_business_address.getText().toString(),
                        "영업시간",seller_business_time.getText().toString(),
                        "전화번호",seller_business_contact_number.getText().toString(),
                        "카테고리",seller_business_category.getSelectedItem().toString(),
                        "휴무일",seller_business_explain.getText().toString()
                );

                //이미지 저장

                if(bitmap!=null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byte_data = baos.toByteArray();

                    UploadTask uploadTask = storageRef.putBytes(byte_data);
                }

                finish();
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
                final String name=data.getStringExtra("상품이름");
                final String count =data.getStringExtra("개수");
                final String price= data.getStringExtra("가격");
                l=new LoadingDialog(this);
                l.setLoadingText("로딩중")
                        .setSuccessText("완료")
                        .setInterceptBack(true)
                        .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                        .show();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()  {
                    public void run() {
                        final Map<String,Object> item = new HashMap<>();
                        item.put("상품이름",name);
                        item.put("개수",count);
                        item.put("가격",price);
                        things.document(name).set(item);
                        l.close();// 시간 지난 후 실행할 코딩
                    }
                }, 1700);


            }
        }
        else if(requestCode==IMAGE_REQUEST)
        {
            if(resultCode==RESULT_OK)
            {
                try {
                    InputStream in=getContentResolver().openInputStream(data.getData());

                    bitmap= BitmapFactory.decodeStream(in);
                    in.close();
                    seller_business_image.setImageBitmap(bitmap);

                    seller_business_image.setDrawingCacheEnabled(true);
                    seller_business_image.buildDrawingCache();

                }
                catch (Exception e){
                    Toast.makeText(this,"사진 선택 에러",Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this,"사진 선택 취소",Toast.LENGTH_LONG).show();
            }
        }
    }

}
