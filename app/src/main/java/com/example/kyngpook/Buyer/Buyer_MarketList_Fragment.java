package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

public class Buyer_MarketList_Fragment extends Fragment {
    Buyer_MainActivity activity;
    ViewGroup rootView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LinearLayout AddressBtn;

    private String address1 = "";
    private String address2 = "";
    private String Category = "가구";
    LoadingDialog l;

    private Buyer_MarketList_Adapter RVadapter;
    private Spinner spinner;
    private SharedPreferenceUtil util;
    private TextView address_text;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Buyer_MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        l=new LoadingDialog(getContext());
        rootView = (ViewGroup) inflater.inflate(R.layout.activity_buyer__market_list, container, false);
        util = new SharedPreferenceUtil(activity);
        //주소 등록 버튼
        address_text=rootView.findViewById(R.id.Buyer_Address_TextView);
        AddressBtn = rootView.findViewById(R.id.Buyer_MarketListAcvitity_AddressBtn);
        AddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startAddressRegistActivity();
            }
        });



        //카테고리 선택 스피너
        RecyclerView recyclerView = rootView.findViewById(R.id.Buyer_MarketListAcvitity_RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        RVadapter = new Buyer_MarketList_Adapter(activity);
        recyclerView.setAdapter(RVadapter);

        String[] items = new String[]{"가구", "꽃", "문구", "생필품", "의류", "주방용품", "철물","음식","기타"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, items);
        spinner = rootView.findViewById(R.id.Buyer_MarketListAcvitity_Spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category = (String) parent.getItemAtPosition(position);
                if (!(address1.equals("") || address2.equals(""))) {
                    final String address = address1 + " " + address2;
                    RVadapter.clear();
                    //해당 카테고리에 따른 Firestore 호출...
                    l.setLoadingText("로딩중")
                            .setSuccessText("완료")
                            .setInterceptBack(true)
                            .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                            .show();
                    db.collection("USERS").document("Seller").collection("Seller")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String tmp1 = (String) document.getData().get("주소");
                                            String[] tmp2 = tmp1.split(" ");
                                            if(tmp2.length > 1) {
                                                String tmp3 = tmp2[0] + " " + tmp2[1];
                                                if (tmp3.equals(address)
                                                        && document.getData().get("카테고리").toString().equals(Category))
                                                {
                                                    String ID = (String) document.getData().get("ID");
                                                    String store = (String) document.getData().get("업소명");
                                                    String storeaddress = (String) document.getData().get("주소");
                                                    String ot = (String) document.getData().get("영업시간");
                                                    String h = (String) document.getData().get("휴무일");
                                                    String name = (String) document.getData().get("대표자명");
                                                    String call = (String) document.getData().get("전화번호");
                                                    int review = Integer.valueOf(document.getData().get("리뷰고유값").toString());
                                                    Buyer_Seller bs = new Buyer_Seller(ID, store, storeaddress, ID + ".jpg", ot, h, address1, address2
                                                            , name, call, review);
                                                    RVadapter.addItem(bs);
                                                    RVadapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    } else {
//                                        Log.w("Buyer_MarketListActiviry", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable()  {
                        public void run() {
                            l.close();// 시간 지난 후 실행할 코딩
                        }
                    }, 1700);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("NONONO", "noThing");
            }
        });

        return rootView;
    }



    public void getResultFromActivity(String address11, String address22, String address33) {
        address_text.setText(address11 + " " + address22 + "\n" + address33);
        this.address1 = address11;
        this.address2 = address22;
        this.util.setStringData("주소", address1 + " " + address2 + "\n" + address33);
        RVadapter.clear();
        db.collection("USERS").document("Seller").collection("Seller")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String tmp1 = (String) document.getData().get("주소");
                                String[] tmp2 = tmp1.split(" ");
                                if(tmp2.length > 1) {
                                    String tmp3 = tmp2[0] + " " + tmp2[1];
                                    if (tmp3.equals(address1 + " " + address2)
                                            && document.getData().get("카테고리").toString().equals(Category)) {
                                        String ID = (String) document.getData().get("ID");
                                        String store = (String) document.getData().get("업소명");
                                        String storeaddress = (String) document.getData().get("주소");
                                        String ot = (String) document.getData().get("영업시간");
                                        String h = (String) document.getData().get("휴무일");
                                        String name = (String) document.getData().get("대표자명");
                                        String call = (String) document.getData().get("전화번호");
                                        int review = Integer.valueOf(document.getData().get("리뷰고유값").toString());

                                        Buyer_Seller bs = new Buyer_Seller(ID, store, storeaddress, ID + ".jpg", ot, h, address1, address2
                                                , name, call, review);
                                        RVadapter.addItem(bs);
                                        RVadapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } else {
//                            Log.w("Buyer_MarketListActiviry", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}