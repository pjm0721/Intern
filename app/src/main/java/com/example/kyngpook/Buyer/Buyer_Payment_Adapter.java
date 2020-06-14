package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_Payment_Adapter extends RecyclerView.Adapter<Buyer_Payment_Adapter.ItemViewHolder> {
    private Buyer_PaymentActivity context;

    private ArrayList<ITEM> listData = new ArrayList<>();

    public Buyer_Payment_Adapter(Context context) {
        this.context = (Buyer_PaymentActivity) context;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return ViewHolder
        //여기서 R.layout.item.xml 설정해주고
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_shoppingactivity_item, parent, false);
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

    public void addItem(ITEM data) {
        listData.add(data);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        //뷰들 선언해주고
        private TextView nameText;
        private TextView numberText;
        private TextView priceText;
        private Button button;
        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.Buyer_Shopping_item_nameText);
            numberText = itemView.findViewById(R.id.Buyer_Shopping_item_numText);
            priceText = itemView.findViewById(R.id.Buyer_Shopping_item_priceText);
            button = itemView.findViewById(R.id.Buyer_Shopping_item_addBtn);
        }
        //처리하면 댐.
        void onBind(ITEM data) {
            nameText.setText(data.name);
            numberText.setText(String.valueOf(data.nownum));
            DecimalFormat formatter = new DecimalFormat("###,###");
            int tot = Integer.valueOf(data.price) * data.nownum;
            priceText.setText(formatter.format(tot) + " 원");
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setText("");
            button.setClickable(false);
        }
    }

    public ArrayList<ITEM> getListData() {
        return listData;
    }

    public void setListData(ArrayList<ITEM> listData) {
        this.listData = listData;
    }
}




