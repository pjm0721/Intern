package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_MarketList_Adapter extends RecyclerView.Adapter<Buyer_MarketList_Adapter.ItemViewHolder> {
    private Context context;

    private ArrayList<Buyer_Seller> listData = new ArrayList<>();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    public Buyer_MarketList_Adapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_marketlistactivity_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        holder.onBind(listData.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Buyer_ShoppingActivity.class);
                intent.putExtra("ID", listData.get(position).ID);
                intent.putExtra("판매자주소", listData.get(position).storeAddress);
                intent.putExtra("주소1", listData.get(position).Address1);
                intent.putExtra("주소2", listData.get(position).Address2);
                intent.putExtra("대표자명", listData.get(position).RepresentativeName);
                intent.putExtra("전화번호", listData.get(position).CallNum);
                intent.putExtra("리뷰", listData.get(position).ReviewNum);
                intent.putExtra("업소명", listData.get(position).StoreName);
                intent.putExtra("영업시간", listData.get(position).OnTime);
                intent.putExtra("휴무일",listData.get(position).Holiday);
                intent.putExtra("제휴할인", listData.get(position).sale);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(Buyer_Seller data) {
        listData.add(data);
        notifyDataSetChanged();
    }

    public void clear() {
        listData.clear();
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView storeName;
        private TextView Time;
        private ImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);

            storeName = itemView.findViewById(R.id.Buyer_MArketList_item_NameTextView);
            Time = itemView.findViewById(R.id.Buyer_MArketList_item_TimeTextView);
            imageView = itemView.findViewById(R.id.Buyer_MArketList_item_ImageView);
        }

        void onBind(Buyer_Seller data) {
            storeName.setText(data.StoreName);
            Time.setText(data.OnTime + " " + data.Holiday);
            String id = data.ID;
            String img = data.img;
            storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + id + "/" + img);

            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(context)
                                .load(task.getResult())
                                .into(imageView);
                    } else {
//                        Log.d("Buyer_MarketList_Adapter", "Glide Error");
                    }
                }
            });

        }
    }

    public ArrayList<Buyer_Seller> getListData() {
        return listData;
    }

    public void setListData(ArrayList<Buyer_Seller> listData) {
        this.listData = listData;
    }
}