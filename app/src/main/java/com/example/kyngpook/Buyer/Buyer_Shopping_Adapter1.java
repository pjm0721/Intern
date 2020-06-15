package com.example.kyngpook.Buyer;

import com.bumptech.glide.Glide;
import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_Shopping_Adapter1 extends RecyclerView.Adapter<Buyer_Shopping_Adapter1.ItemViewHolder> {
    private Buyer_ShoppingActivity context;
    private String SellerID;
    private ArrayList<ITEM> listData = new ArrayList<>();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;


    public  Buyer_Shopping_Adapter1(Context context, String SellerID) {
        this.context = (Buyer_ShoppingActivity) context;
        this.SellerID = SellerID;
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
        private Button addBtn;
        private ImageView imageView;

        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.Buyer_Shopping_item_nameText);
            numberText = itemView.findViewById(R.id.Buyer_Shopping_item_numText);
            priceText = itemView.findViewById(R.id.Buyer_Shopping_item_priceText);
            addBtn = itemView.findViewById(R.id.Buyer_Shopping_item_addBtn);
            imageView = itemView.findViewById(R.id.Buyer_Shopping_item_imgView);
        }
        //처리하면 댐.
        void onBind(final ITEM data) {
            nameText.setText(data.name);
            numberText.setText(data.number+"개");
            DecimalFormat formatter = new DecimalFormat("###,###");
            priceText.setText(formatter.format(Integer.valueOf(data.price)) + " 원");
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.addItemToTempBasket(data);
                }
            });

            storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + SellerID + "/" + data.name + ".jpg");

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

    public ArrayList<ITEM> getListData() {
        return listData;
    }

    public void setListData(ArrayList<ITEM> listData) {
        this.listData = listData;
    }
}




