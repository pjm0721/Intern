package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Buyer_ShoppingActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Buyer_Shopping_Adapter2 adapter2;

    private TextView totalPriceText;
    private int sale = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer__shopping);

        final Intent intent = getIntent();
        final String ID = intent.getStringExtra("ID");
        String address1 = intent.getStringExtra("주소1");
        String address2 = intent.getStringExtra("주소2");
        final String name = intent.getStringExtra("대표자명");
        final String storename = intent.getStringExtra("업소명");
        final int review = intent.getIntExtra("리뷰", 0);
        final String call = intent.getStringExtra("전화번호");
        final String time = intent.getStringExtra("영업시간");
        final String StoreAddress = intent.getStringExtra("판매자주소");
        final String holiday=intent.getStringExtra("휴무일");
        sale = intent.getIntExtra("제휴할인", 0);

        totalPriceText = (TextView) findViewById(R.id.Buyer_Shopping_totalPriceText);
        totalPriceText.setText("총 상품금액 : 0 원");
        //판매자 정보 로드
        TextView holidayText=(TextView)findViewById(R.id.Buyer_Shopping_holidayText);
        holidayText.setText("휴무일 : "+holiday);

        TextView storenameText = (TextView)findViewById(R.id.Buyer_Shopping_StoreNameText);
        storenameText.setText(storename);

        TextView timeText = (TextView)findViewById(R.id.Buyer_Shopping_timeText);
        timeText.setText("영업시간 : "+time);

        TextView callText = (TextView)findViewById(R.id.Buyer_Shopping_callText);
        callText.setText("전화번호 : " + call);

        TextView pText = (TextView)findViewById(R.id.Buyer_Shopping_PresentativeText);
        pText.setText("대표자명 : " +  name);

        TextView sText = (TextView) findViewById(R.id.Buyer_Shopping_SaleText);
        sText.setText("제휴할인 : 총 금액의 " + sale +"% 할인");
        //리뷰 버튼 구현
        findViewById(R.id.Buyer_Shopping_ReviewBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Buyer_ReviewActivity.class);
                intent1.putExtra("ID", ID);
                intent1.putExtra("대표자명", name);
                intent1.putExtra("전화번호", call);
                intent1.putExtra("업소명", storename);
                intent1.putExtra("영업시간", time);
                intent1.putExtra("리뷰", review);
                intent1.putExtra("휴무일",holiday);
                intent1.putExtra("제휴할인", sale);
                startActivity(intent1);
            }
        });
        //판매 물품 정보 로드
        RecyclerView listRecyclerView = (RecyclerView) findViewById(R.id.Buyer_Shopping_productRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listRecyclerView.setLayoutManager(linearLayoutManager);
        final Buyer_Shopping_Adapter1 adapter1 = new Buyer_Shopping_Adapter1(this, ID);
        listRecyclerView.setAdapter(adapter1);
        db.collection("PRODUCT").document(address1).collection(address2)
                .document(ID).collection("판매상품")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId();
                                String price = (String) document.getData().get("가격");
                                String number = (String) document.getData().get("개수");
                                ITEM item = new ITEM(name, price, number);
                                adapter1.addItem(item);
                                adapter1.notifyDataSetChanged();
                            }
                        } else {
                            //Log.w("Buyer_MarketListActiviry", "Error getting documents.", task.getException());
                        }
                    }
                });

        //임시 장바구니
        RecyclerView BusketRecyclerView = (RecyclerView) findViewById(R.id.Buyer_Shopping_basketRecyclerView);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        BusketRecyclerView.setLayoutManager(linearLayoutManager2);
        adapter2 = new Buyer_Shopping_Adapter2(this);
        BusketRecyclerView.setAdapter(adapter2);

        findViewById(R.id.Buyer_Shopping_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int listCount = adapter2.getItemCount();
                if(listCount > 0) {
                    Intent intent = new Intent(getApplicationContext(), Buyer_PaymentActivity.class);
                    ArrayList<ITEM> list = adapter2.getListData();
                    intent.putExtra("판매자번호", call);
                    intent.putExtra("장바구니", list);
                    intent.putExtra("판매자주소", StoreAddress);
                    intent.putExtra("업소명", storename);
                    intent.putExtra("판매자아이디", ID);
                    intent.putExtra("제휴할인", sale);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), "상품을 담아주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int calculate() {
        ArrayList<ITEM> l = adapter2.getListData();
        int total = 0;
        for(int i = 0; i < l.size(); i++) {
            total += l.get(i).nownum * Integer.valueOf(l.get(i).price);
        }
        return (int) (total * (100 - sale) * 0.01);
    }
    public void addTotalPrice() {
        DecimalFormat formatter = new DecimalFormat("###,###");
        totalPriceText.setText("총 상품금액 : " + formatter.format(Integer.valueOf(calculate())) + " 원");
    }
    public void addItemToTempBasket(ITEM item) {
        if(Integer.valueOf(item.number) == 0) {
            Toast.makeText(Buyer_ShoppingActivity.this, "재고가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<ITEM> l = adapter2.getListData();
        for(int i = 0; i < l.size(); i++) {
            if(item.equals(l.get(i)))
                return;
        }
        adapter2.addItem(item);
        adapter2.notifyDataSetChanged();

        DecimalFormat formatter = new DecimalFormat("###,###");
        totalPriceText.setText("총 상품금액 : " + formatter.format(Integer.valueOf(calculate())) + " 원");
    }
}
class ITEM implements Parcelable {
    String name, price, number;
    int nownum;
    public ITEM(String name, String price, String number) {
        this.name = name; this.price = price; this.number = number;
    }

    protected ITEM(Parcel in) {
        name = in.readString();
        price = in.readString();
        number = in.readString();
        nownum = in.readInt();
    }

    public static final Creator<ITEM> CREATOR = new Creator<ITEM>() {
        @Override
        public ITEM createFromParcel(Parcel in) {
            return new ITEM(in);
        }

        @Override
        public ITEM[] newArray(int size) {
            return new ITEM[size];
        }
    };

    void setnum(int n) {this.nownum = n;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(number);
        dest.writeInt(nownum);
    }
}