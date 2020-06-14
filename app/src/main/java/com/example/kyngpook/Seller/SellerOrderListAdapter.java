package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//판매자 주문 내역 리스트 어뎁터
public class SellerOrderListAdapter extends RecyclerView.Adapter<SellerOrderListAdapter.ItemViewHolder> {
    private Context context;

    private ArrayList<SellerOrderListData> listData = new ArrayList<>();

    public SellerOrderListAdapter(Context context) {this.context = context;}

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //여기서 R.layout.item.xml 설정해주고 데이터 로딩
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_total_order_list_style, parent, false);
        return new ItemViewHolder(view);
    }

    //리스트뷰 선택 시 상세보기 액티비티 실행
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        holder.onBind(listData.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),SellerOrderCheckAlert.class);

                intent.putExtra("구매자주소",listData.get(position).address);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(SellerOrderListData data) {
        listData.add(data);
        notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        //뷰들 선언해주고
        private TextView seller_order_list_price;
        private TextView seller_order_list_state;
        private TextView seller_order_list_address;
        private LinearLayout seller_order_list_layout;
        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            seller_order_list_price = itemView.findViewById(R.id.seller_order_list_price);
            seller_order_list_state = itemView.findViewById(R.id.seller_order_list_state);
            seller_order_list_address = itemView.findViewById(R.id.seller_order_list_address);
            seller_order_list_layout=itemView.findViewById(R.id.seller_order_list_layout);

        }
        //처리하면 댐.
        void onBind(SellerOrderListData data) {
            seller_order_list_price.setText(" 금액 : "+data.price);
            seller_order_list_state.setText(" 주문상태 : "+data.state);
            seller_order_list_address.setText(" 주소 : "+data.address);

        }
    }



    public ArrayList<SellerOrderListData> getListData() {
        return listData;
    }

    public void setListData(ArrayList<SellerOrderListData> listData) {
        this.listData = listData;
    }
}
