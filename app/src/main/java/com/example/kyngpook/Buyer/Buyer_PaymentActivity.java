package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Buyer_PaymentActivity extends AppCompatActivity {
    private SharedPreferenceUtil util;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer__payment);
        //구매자 정보 로드 (SharedPreference)
        util = new SharedPreferenceUtil(this);

        TextView idText = (TextView) findViewById(R.id.Buyer_PaymentActivity_idText);
        TextView callText = (TextView) findViewById(R.id.Buyer_PaymentActivity_callText);
        TextView addressText = (TextView) findViewById(R.id.Buyer_PaymentActivity_addressText);
        final String id1 = util.getStringData("ID", "id1");
        final String call = util.getStringData("전화번호", "01012341234");
        final String address = util.getStringData("주소", "null");
        final String[] addressSplit = address.split("\n");
        final String address1 = addressSplit[0];
        final String address2 = addressSplit[1];

        final String realName = util.getStringData("이름", "홍길동");
        idText.setText(id1);

        callText.setText(call.substring(0, 3) + "-" + call.substring(3, 7) + "-" + call.substring(7));
        addressText.setText(address);
        TextView nameText = (TextView) findViewById(R.id.Buyer_PaymentActivity_nameText);
        nameText.setText(realName);

        final String password = util.getStringData("PASSWORD", "");

        //상품 정보 로드
        Intent intent = getIntent();
        final String storeAddress = intent.getStringExtra("판매자주소");
        final String storeName = intent.getStringExtra("업소명");
        final String sellerID = intent.getStringExtra("판매자아이디");
        final String SellerCall = intent.getStringExtra("판매자번호");

        final ArrayList<ITEM> list = (ArrayList<ITEM>) intent.getSerializableExtra("장바구니");

        RecyclerView recyclerView = findViewById(R.id.Buyer_PaymentActivity_RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        final Buyer_Payment_Adapter adapter = new Buyer_Payment_Adapter(this);
        adapter.setListData(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        TextView textView = (TextView) findViewById(R.id.Buyer_PaymentActivity_totText);
        int total = 1000;
        for (int i = 0; i < list.size(); i++) {
            total += list.get(i).nownum * Integer.valueOf(list.get(i).price);
        }
        DecimalFormat formatter = new DecimalFormat("###,###");
        textView.setText("총 결제금액 : "+formatter.format(total) + " 원");

        //결제 방식
        String[] items = new String[]{"만나서 카드 결제", "만나서 현금 결제","카드 결제", "카카오페이", "네이버 페이", "휴대폰 결제", "삼성 페이"};
        final ArrayAdapter<String> adapter1111 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
        final Spinner spinner = (Spinner) findViewById(R.id.Buyer_PaymentActivity_Spinner);
        spinner.setAdapter(adapter1111);

        //결제 버튼
        final int finalTotal = total;
        findViewById(R.id.Buyer_PaymentActivity_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText et = new EditText(Buyer_PaymentActivity.this);
                et.setHint("암호를 입력하세요.");
                et.setHintTextColor(Color.YELLOW);
                FrameLayout container = new FrameLayout(Buyer_PaymentActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                et.setLayoutParams(params);
                container.addView(et);
                final AlertDialog.Builder alt_bld = new AlertDialog.Builder(Buyer_PaymentActivity.this, R.style.MyAlertDialogStyle);
                alt_bld.setTitle("결 제 암 호")
                        .setView(container).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String value = et.getText().toString();
                        //암호 비교하기
                        if(value.equals(password)) {
                            //문자 보내기
                            String phoneNumber = SellerCall;
                            String smsBody1 = "<코노노> 주문입니다!\n";
                            smsBody1 += "구매자 : " + realName + "\n";
                            smsBody1 += "주소 : " + address1 + " " + address2 + "\n";
                            smsBody1 += "주문내역 : " + list.get(0).name + " 외 " + String.valueOf(list.size() - 1) + "건";
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNumber, null, smsBody1, null, null);
                            } catch (Exception e) {
                                Log.w("ServiceThread", "메세지 전송 오류");
                                e.printStackTrace();
                            }

                            ActivityCompat.finishAffinity(Buyer_PaymentActivity.this);

                            //DB 주문내역에 추가하기
                            Map<String, Object> order = new HashMap<>();
                            order.put("구매자아이디", id1);
                            order.put("구매자주소", address1 + " " + address2);
                            order.put("금액", finalTotal);
                            order.put("리뷰여부", false);
                            order.put("업소명", storeName);
                            order.put("주문상태", "주문대기");
                            order.put("구매자이름", realName);
                            SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy년 MM월dd일 HH:mm:ss");
                            Date time = new Date();
                            String time2 = format2.format(time);
                            order.put("주문시간", time2);
                            order.put("판매자아이디", sellerID);
                            order.put("판매자주소", storeAddress);
                            order.put("배달현황", false);
                            order.put("배달자담당아이디", "");
                            order.put("결제방식", spinner.getSelectedItem().toString());

                            Map<String, Object> orderInfo = new HashMap<>();
                            ArrayList<ITEM> listData = adapter.getListData();
                            for(int i = 0; i < listData.size(); i++) {
                                orderInfo.put(listData.get(i).name, listData.get(i).nownum);
                            }
                            order.put("주문내역", orderInfo);


                            SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.SSS");
                            String doc = format.format(time);
                            order.put("문서이름", doc);
                            db.collection("주문내역").document(doc)
                                    .set(order)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Buyer_PaymentActivity-O", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Buyer_PaymentActivity-O", "Error writing document", e);
                                        }
                                    });

                            Intent intent1 = new Intent(Buyer_PaymentActivity.this, Buyer_PayCompleteActivity.class);
                            startActivity(intent1);
                        }
                        else
                            Toast.makeText(Buyer_PaymentActivity.this, "암호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        });
    }
}