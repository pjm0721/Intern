package com.example.kyngpook.Seller;

import com.bumptech.glide.Glide;
import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/// 판매자 메뉴 관리 어뎁터
public class SellerRMListAdapter extends RecyclerView.Adapter<SellerRMListAdapter.ItemViewHolder> {
    private Context context;
    private CollectionReference cr;

    FirebaseStorage storage;
    StorageReference storageRef;

    private ArrayList<SellerRMListData> listData = new ArrayList<>();

    public SellerRMListAdapter(Context context, ArrayList<SellerRMListData> listData)
    {
        this.context = context;
        this.listData=listData;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_modifier_list_item_style, parent, false);
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        //뷰들 선언해주고
        private TextView seller_modify_list_name;
        private TextView seller_modify_list_num;
        private TextView seller_modify_list_price;
        private CheckBox seller_modifiy_list_checkbox;
        private ImageView seller_modify_list_image;

        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            seller_modify_list_name = itemView.findViewById(R.id.seller_modify_list_name);
            seller_modify_list_num = itemView.findViewById(R.id.seller_modify_list_num);
            seller_modify_list_price = itemView.findViewById(R.id.seller_modify_list_price);
            seller_modifiy_list_checkbox = itemView.findViewById(R.id.seller_modifiy_list_checkbox);
            seller_modify_list_image=itemView.findViewById(R.id.seller_modify_list_image);
        }
        //처리하면 댐.
        void onBind(final SellerRMListData data) {
            seller_modify_list_name.setText(" 물품 : "+data.name);
            seller_modify_list_num.setText(" 재고 : "+data.num+" 개");
            seller_modify_list_price.setText(" 가격 : "+data.price+" 원");


            seller_modifiy_list_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.check=!data.check;
                }
            });

            SharedPreferences pref = context.getSharedPreferences("seller", MODE_PRIVATE);
            String seller_ID = pref.getString("id","");

            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + seller_ID + "/" + data.name + ".jpg");
            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(context)
                                .load(task.getResult())
                                .into(seller_modify_list_image);
                    }
                    else
                    {Log.d("SellerRMA", "Glide Error"); }
                }
            });

        }
    }

}
