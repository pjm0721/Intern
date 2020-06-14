package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_Review_Adapter extends RecyclerView.Adapter<Buyer_Review_Adapter.ItemViewHolder> {

    private ArrayList<Buyer_ReviewActivity.Buyer_Review> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return ViewHolder
        //여기서 R.layout.item.xml 설정해주고
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_reviewactivity_item, parent, false);
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

    public void addItem(Buyer_ReviewActivity.Buyer_Review data) {
        listData.add(data);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        //뷰들 선언해주고
        private TextView nickName;
        private TextView Time;
        private TextView Content;
        private TextView Score;
        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);

            nickName = itemView.findViewById(R.id.Buyer_Review_item_nicknameText);
            Time = itemView.findViewById(R.id.Buyer_Review_item_timeText);
            Content = itemView.findViewById(R.id.Buyer_Review_item_contentText);
            Score = itemView.findViewById(R.id.Buyer_Review_item_scoreText);
        }
        //처리하면 댐.
        void onBind(Buyer_ReviewActivity.Buyer_Review data) {
            nickName.setText(data.nickname);
            Time.setText(data.time);
            Content.setText(data.content);
            Score.setText("평점 : " + data.score + ".0 / 5.0");
        }
    }

    public ArrayList<Buyer_ReviewActivity.Buyer_Review> getListData() {
        return listData;
    }

    public void setListData(ArrayList<Buyer_ReviewActivity.Buyer_Review> listData) {
        this.listData = listData;
    }
}




