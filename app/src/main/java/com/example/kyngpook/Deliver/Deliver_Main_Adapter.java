package com.example.kyngpook.Deliver;

import com.example.kyngpook.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Deliver_Main_Adapter extends BaseAdapter {
    private Context context;
    private List<Deliver_Main_Item> boardlist;

    public Deliver_Main_Adapter(Context context, List<Deliver_Main_Item> boardlist) {
        this.context = context;
        this.boardlist = boardlist;
    }

    @Override
    public int getCount() {
        return boardlist.size();
    }

    @Override
    public Object getItem(int position) {
        return boardlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.deliver_activity_list_item, null );

        TextView dma_seller_address_text = (TextView)v.findViewById(R.id.deliver_activity_list_item_seller_address);
        TextView dma_buyer_address_text = (TextView)v.findViewById(R.id.deliver_activity_list_item_buyer_address);
        TextView dma_seller_name_text = (TextView)v.findViewById(R.id.deliver_activity_list_item_seller_name);

        dma_seller_address_text.setText("출발지 : " + boardlist.get(position).getDMI_Seller_Address());
        dma_buyer_address_text.setText("도착지 : " + boardlist.get(position).getDMI_Buyer_Address());
        dma_seller_name_text.setText(boardlist.get(position).getDMI_Seller_Name());

        return v;
    }
}
