package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_Shopping_Adapter2 extends RecyclerView.Adapter<Buyer_Shopping_Adapter2.ItemViewHolder> {
    private Buyer_ShoppingActivity context;
    private ArrayList<ITEM> listData = new ArrayList<>();

    public Buyer_Shopping_Adapter2(Context context) {
        this.context = (Buyer_ShoppingActivity) context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return ViewHolder
        //여기서 R.layout.item.xml 설정해주고
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_shoppingactivity_item2, parent, false);
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
        ITEM i = data;
        i.setnum(1);
        listData.add(i);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        //뷰들 선언해주고
        private TextView nameText;
        private TextView numberText;
        private TextView priceText;
        private Button upBtn, downBtn, removeBtn;

        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.Buyer_Shopping_item2_nameText);
            numberText = itemView.findViewById(R.id.Buyer_Shopping_item2_numText);
            priceText = itemView.findViewById(R.id.Buyer_Shopping_item2_priceText);
            upBtn = itemView.findViewById(R.id.Buyer_Shopping_item2_upBtn);
            downBtn = itemView.findViewById(R.id.Buyer_Shopping_item2_downBtn);
            removeBtn = itemView.findViewById(R.id.Buyer_Shopping_item2_removeBtn);
        }

        //처리하면 댐.
        void onBind(final ITEM data) {
            Log.d("NONONO-2", String.valueOf(data.hashCode()));
            nameText.setText(data.name);
            numberText.setText(String.valueOf(data.nownum));
            final DecimalFormat formatter = new DecimalFormat("###,###");

            int p = Integer.valueOf(data.price);
            int tot = data.nownum * p;
            priceText.setText(formatter.format(tot) + " 원");

            upBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberText.getText().toString());
                    int max = Integer.valueOf(data.number);
                    if (n < max) {
                        ++n;
                        data.setnum(n);
                        numberText.setText(String.valueOf(data.nownum));
                        int p = Integer.valueOf(data.price);
                        int tot = data.nownum * p;
                        priceText.setText(formatter.format(tot) + " 원");
                        context.addTotalPrice();
                    }
                }
            });
            downBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = Integer.valueOf(numberText.getText().toString());
                    if (n > 1) {
                        --n;
                        data.setnum(n);
                        numberText.setText(String.valueOf(data.nownum));
                        int p = Integer.valueOf(data.price);
                        int tot = data.nownum * p;
                        priceText.setText(formatter.format(tot) + " 원");
                        context.addTotalPrice();
                    }
                }
            });
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listData.remove(data);
                    notifyDataSetChanged();
                    context.addTotalPrice();
                }
            });
        }
    }

    public ArrayList<ITEM> getListData() {
        return listData;
    }

    public void setListData(ArrayList<ITEM> listData) {
        this.listData = listData;
    }
}