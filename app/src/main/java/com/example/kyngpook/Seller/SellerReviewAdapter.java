package com.example.kyngpook.Seller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kyngpook.R;

import java.util.List;

public class SellerReviewAdapter extends BaseAdapter {
    private Context context;
    private List<SellerReviewItem> boardlist;

    public SellerReviewAdapter(Context context, List<SellerReviewItem> boardlist) {
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
        View v = View.inflate(context, R.layout.seller_review_item_view, null);

        TextView sra_nickname = (TextView)v.findViewById(R.id.sriv_nickname);
        TextView sra_time = (TextView)v.findViewById(R.id.sriv_time);

        sra_nickname.setText(boardlist.get(position).getSri_nickname());
        sra_time.setText(boardlist.get(position).getSri_time());
        return v;
    }
}
