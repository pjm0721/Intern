package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// 판매자 주문내역 상세보기 내에 요청상품 리스트를 위한 어뎁터
public class SellerOrderAlertListAdapter extends RecyclerView.Adapter<SellerOrderAlertListAdapter.ItemViewHolder> {
    private Context context;

    private ArrayList<SellerOALData> listData = new ArrayList<>();

    public SellerOrderAlertListAdapter(Context context) {this.context = context;}

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //여기서 R.layout.item.xml 설정해주고 데이터 로딩
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_order_alert_itemstyle, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(SellerOALData data) {
        listData.add(data);
        notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        //뷰들 선언해주고
        private TextView seller_order_alert_item;

        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            seller_order_alert_item = itemView.findViewById(R.id.seller_order_alert_item);
        }
        //처리하면 댐.
        void onBind(SellerOALData data) {
            seller_order_alert_item.setText(data.name+" "+data.num+" 개");
        }
    }

    public ArrayList<SellerOALData> getListData() {
        return listData;
    }

    public void setListData(ArrayList<SellerOALData> listData) {
        this.listData = listData;
    }
}