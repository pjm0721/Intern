package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_OrderList_Fragment extends Fragment {
    Buyer_MainActivity activity;
    ViewGroup rootView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Buyer_OrderList_Adapter adapter;

    private SharedPreferenceUtil util;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Buyer_MainActivity)getActivity();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.activity_buyer__order_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.Buyer_OrderListActivity_RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Buyer_OrderList_Adapter(activity);
        recyclerView.setAdapter(adapter);
        util = new SharedPreferenceUtil(activity);
        final String ID = util.getStringData("ID", "id1");

        Log.d("NONONO", "NONONO");
        //주문 내역 DB 로드
        db.collection("주문내역").orderBy("주문시간", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("구매자아이디").toString().equals(ID))
                                {
                                    String store = (String) document.getData().get("업소명");
                                    String time = (String) document.getData().get("주문시간");
                                    String state = (String) document.getData().get("주문상태");
                                    String address = (String) document.getData().get("구매자주소");
                                    String order = "";
                                    Map orderMap = (Map) document.getData().get("주문내역");
                                    for(Object keyy : orderMap.keySet()) {
                                        String key = (String) keyy;
                                        order += key + "×" + orderMap.get(key).toString()+" ";
                                    }

                                    int price = Integer.valueOf(document.getData().get("금액").toString());
                                    boolean review = (boolean) document.getData().get("리뷰여부");
                                    String id = (String) document.getData().get("판매자아이디");
                                    String idd = document.getId();
                                    orderInfo info = new orderInfo(store, time, state, address, order, price, review, id, idd);
                                    adapter.addItem(info);
                                    adapter.notifyDataSetChanged();
                                    Log.d("NONONO", info.toString());
                                }
                            }
                        } else {
//                            Log.w("Buyer_MarketListActiviry", "Error getting documents.", task.getException());
                        }
                    }
                });

        return rootView;
    }



    public void addReviewDb(Map<String, Object> rv, orderInfo info) {
        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy년 MM월dd일 HH:mm:ss");
        Date time = new Date();
        String time2 = format2.format(time);

        //리뷰 추가
        db.collection("USERS").document("Seller").collection("Seller")
                .document(info.sellerID).collection("리뷰")
                .document(time2)
                .set(rv, SetOptions.merge());

        //리뷰여부 수정
        Map<String, Object> data = new HashMap<>();
        data.put("리뷰여부", true);

        db.collection("주문내역").document(info.DB)
                .set(data, SetOptions.merge());
        //어댑터 수정
        ArrayList<orderInfo> listData = adapter.getListData();
        for(int i = 0; i < listData.size(); i++) {
            if(listData.get(i).equals(info)) {
                orderInfo k = info;
                k.review = true;
                listData.set(i, k);
                adapter.setListData(listData);
                break;
            }
        }

    }
}