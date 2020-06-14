package com.example.kyngpook.Deliver;

import com.example.kyngpook.Buyer.Buyer_MainActivity;
import com.example.kyngpook.Login_Signup.LogInActivity;
import com.example.kyngpook.Login_Signup.LoginSharedPreferenceUtil;
import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Deliver_MainActivity extends AppCompatActivity {
    // ListView variables
    private ListView deliver_mainactivity_boardlistview;
    private Deliver_Main_Adapter deliver_mainactivity_adapter;
    private List<Deliver_Main_Item> deliver_mainactivity_boardlist;

    // FireBase FireCloud variables
    private FirebaseFirestore db;

    // StartActivityForResult constants
    private final int DELIVER_REQUEST = 1000;
    private final int RESULT_ACCEPT = 1001;
    private final int RESULT_REJECT = 1002;

    private EditText deliver_mainactivity_searchview;
    private Button deliver_mainactivity_refresh_button;
    private ProgressBar deliver_mainactivity_progressbar;
    private TextView deliver_mainactivity_num;
    private Boolean deliver_process_check = true;
    private long backKeyPressedTime = 0;
    private String seller_phone_number = "";

    @Override
    protected void onResume() {
        // activity가 다시 시작할 때 구현부.
        // 검색내용을 그대로 두고 배달완료한 것만 삭제 후 다시 리스트뷰 생성.
        super.onResume();
        deliver_mainactivity_get_data(deliver_mainactivity_searchview.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliver_activity_main);
        deliver_mainactivity_searchview = (EditText) findViewById(R.id.deliver_activity_main_searchview);
        deliver_mainactivity_progressbar = (ProgressBar) findViewById(R.id.deliver_activity_main_progressbar);
        deliver_mainactivity_boardlistview = (ListView) findViewById(R.id.deliver_activity_main_listview);
        deliver_mainactivity_boardlist = new ArrayList<Deliver_Main_Item>();
        deliver_mainactivity_adapter = new Deliver_Main_Adapter(getApplicationContext(), deliver_mainactivity_boardlist);
        deliver_mainactivity_boardlistview.setAdapter(deliver_mainactivity_adapter);
        deliver_mainactivity_num = (TextView) findViewById(R.id.deliver_activity_main_num);
        deliver_mainactivity_get_data(deliver_mainactivity_searchview.getText().toString());
        deliver_mainactivity_refresh_button = (Button) findViewById(R.id.deliver_activity_main_refresh_button);


        // 검색기능 구현부
        deliver_mainactivity_searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                deliver_mainactivity_get_data(deliver_mainactivity_searchview.getText().toString());
            }
        });


        // 새로고침 구현부
        deliver_mainactivity_refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliver_mainactivity_get_data(deliver_mainactivity_searchview.getText().toString());
                Snackbar.make(v, "새로고침이 완료되었습니다!!", Snackbar.LENGTH_LONG).show();
            }
        });


        // ListView item click listener
        deliver_mainactivity_boardlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // 판매자의 ID를 통해서 전화번호를 얻어옴.
                deliver_mainactivity_get_seller_phone(deliver_mainactivity_boardlist.get(position).getDMI_Seller_Id());

                AlertDialog.Builder builder = new AlertDialog.Builder(Deliver_MainActivity.this);
                builder.setTitle("배달을 수락하시겠습니까?")
                        .setMessage("\n출발지 : " + deliver_mainactivity_boardlist.get(position).getDMI_Seller_Address() + "\n\n목적지 : "
                                + deliver_mainactivity_boardlist.get(position).getDMI_Buyer_Address())
                        .setPositiveButton("네! 배달할께요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deliver_process_check = true;
                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        // 한번더 배달중임을 확인해준다.
                                        DocumentSnapshot snapshot = transaction.get(deliver_mainactivity_boardlist.get(position).getDMI_Document_Reference());

                                        if (snapshot.getBoolean("배달현황")) {
                                            Toast.makeText(getApplicationContext(), "이미 배달이 진행중입니다..", Toast.LENGTH_LONG).show();
                                            deliver_process_check = false;
                                            return null;
                                        }

                                        transaction.update(deliver_mainactivity_boardlist.get(position).getDMI_Document_Reference(), "배달현황", true);
                                        return null;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "오류가 발생했습니다.. 다시 시도해주세요..", Toast.LENGTH_LONG).show();
                                    }
                                });

                                if (deliver_process_check) {
                                    // 배달 상세 화면으로 넘어갈 때 그냥 Intent에 값을 넣어서 넘겨준다.
                                    Intent intent = new Intent(Deliver_MainActivity.this, Deliver_Item_Information.class);
                                    intent.putExtra("SELLER_NAME", deliver_mainactivity_boardlist.get(position).getDMI_Seller_Name());
                                    intent.putExtra("SELLER_ID", deliver_mainactivity_boardlist.get(position).getDMI_Seller_Id());
                                    intent.putExtra("SELLER_ADDRESS", deliver_mainactivity_boardlist.get(position).getDMI_Seller_Address());
                                    intent.putExtra("BUYER_NAME", deliver_mainactivity_boardlist.get(position).getDMI_Buyer_Name());
                                    intent.putExtra("BUYER_ID", deliver_mainactivity_boardlist.get(position).getDMI_Buyer_Id());
                                    intent.putExtra("BUYER_ADDRESS", deliver_mainactivity_boardlist.get(position).getDMI_Buyer_Address());
                                    intent.putExtra("PRICE", deliver_mainactivity_boardlist.get(position).getDMI_Price());
                                    intent.putExtra("ORDER_TIME", deliver_mainactivity_boardlist.get(position).getDMI_Order_Time());

                                    // null일 때 전화번호가 없는 데 메시지 보내면 오류발생하니까 null이 아닐때만 처리.
                                    if (seller_phone_number != "") {
                                        Toast.makeText(getApplicationContext(), seller_phone_number, Toast.LENGTH_LONG).show();
                                        /* SMS 전송 테스트
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage("01062817950", null, "test", null, null);
                                        send_sms("01062817950", "test");
                                         */

                                        /* SMS 문자메시지 내용 변수 */
                                        String contents = "";

                                        /* 이거 주석 절대 풀지마세요.. 데이터베이스에 053112도 있던데 그리로 문자갑니다..
                                        send_sms(seller_phone_number, contents);
                                         */
                                    }

                                    // Activity 시작
                                    startActivityForResult(intent, DELIVER_REQUEST);
                                }

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("배달이 힘들꺼같아요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 요청을 거절했을 때 그냥 다이얼로그만 사라지게 구현
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DELIVER_REQUEST) {
            if (resultCode == RESULT_ACCEPT) {
                deliver_mainactivity_get_data(deliver_mainactivity_searchview.getText().toString());
            }
        }
    }
     */


    private void deliver_mainactivity_get_data(final String text) {
        deliver_mainactivity_progressbar.setVisibility(View.VISIBLE);

        // database reading part
        db = FirebaseFirestore.getInstance();
        db.collection("주문내역").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            deliver_mainactivity_boardlist.clear();
                            int deliver_count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference dm_document_ref = document.getReference();
                                String dm_documnet_buyer_id = document.getData().get("구매자아이디").toString();
                                String dm_document_buyer_address = document.getData().get("구매자주소").toString();
                                String dm_document_buyer_name = document.getData().get("구매자이름").toString();
                                int dm_document_price = Integer.parseInt(document.getData().get("금액").toString());
                                boolean dm_document_review = Boolean.parseBoolean(document.getData().get("리뷰여부").toString());
                                String dm_document_seller_name = document.getData().get("업소명").toString();
                                String dm_document_seller_id = document.getData().get("판매자아이디").toString();
                                String dm_document_seller_address = document.getData().get("판매자주소").toString();
                                String dm_document_order_time = document.getData().get("주문시간").toString();
                                Map dm_document_order_info = (Map) document.getData().get("주문내역");
                                boolean dm_document_deliver_status = Boolean.parseBoolean(document.getData().get("배달현황").toString());

                                if (!dm_document_deliver_status) {
                                    if (text.length() == 0) {
                                        deliver_count++;
                                        deliver_mainactivity_boardlist.add(new Deliver_Main_Item(
                                                dm_document_ref, dm_documnet_buyer_id, dm_document_buyer_address, dm_document_buyer_name,
                                                dm_document_price, dm_document_review, dm_document_seller_name, dm_document_seller_id,
                                                dm_document_seller_address, dm_document_order_time, dm_document_order_info
                                        ));
                                    } else {
                                        if (dm_document_seller_address.toLowerCase().contains(text) || dm_document_seller_name.toLowerCase().contains(text)) {
                                            deliver_count++;
                                            deliver_mainactivity_boardlist.add(new Deliver_Main_Item(
                                                    dm_document_ref, dm_documnet_buyer_id, dm_document_buyer_address, dm_document_buyer_name,
                                                    dm_document_price, dm_document_review, dm_document_seller_name, dm_document_seller_id,
                                                    dm_document_seller_address, dm_document_order_time, dm_document_order_info
                                            ));
                                        }
                                    }
                                }
                            }
                            deliver_mainactivity_num.setText(Integer.toString(deliver_count));
                            deliver_mainactivity_adapter.notifyDataSetChanged();
                        }
                    }
                });
        deliver_mainactivity_progressbar.setVisibility(View.INVISIBLE);
    }

    private void deliver_mainactivity_get_seller_phone(final String seller_id) {
        seller_phone_number = "";
        db = FirebaseFirestore.getInstance();
        db.collection("USERS").document("Seller").collection("Seller")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String temp_seller_id = document.get("ID").toString();
                                String temp_phone_number = document.get("전화번호").toString();

                                if (temp_seller_id.equals(seller_id)) {
                                    seller_phone_number += temp_phone_number;
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Toast toast;
        toast = Toast.makeText(this, "초기화", Toast.LENGTH_SHORT);
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "로그아웃 하시겠습니까?", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            LoginSharedPreferenceUtil util11 =  new LoginSharedPreferenceUtil(this);
            Boolean goToLogin = util11.getBooleanData("AutoLogin", false);

            util11.setBooleanData("AutoLogin", false);
            util11.setStringData("ID", "");
            util11.setStringData("권한", "null");
            if(goToLogin) {
                startActivity(new Intent(Deliver_MainActivity.this, LogInActivity.class));
            }
            finish();
            toast.cancel();
        }
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
