package com.example.kyngpook.Seller;

import com.bumptech.glide.Glide;
import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import static android.content.Context.MODE_PRIVATE;

/// 판매자 메뉴 관리 어뎁터
public class SellerRMListAdapter extends RecyclerView.Adapter<SellerRMListAdapter.ItemViewHolder> {
    private Context context;

    FirebaseStorage storage;
    StorageReference storageRef;



    private ArrayList<SellerRMListData> listData = new ArrayList<>();

    public SellerRMListAdapter(Context context) {this.context = context;}

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return ViewHolder
        //여기서 R.layout.item.xml 설정해주고
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

    public void addItem(SellerRMListData data) {
        listData.add(data);
        notifyDataSetChanged();
    }

    public void checkRemoveAll(){

        SharedPreferences pref = context.getSharedPreferences("seller", MODE_PRIVATE);
        String seller_ID = pref.getString("id","");
        for(int i=listData.size()-1;i>=0;i--)
        {
            if(listData.get(i).check) {

                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + seller_ID + "/" + listData.get(i).name + ".jpg");
                storageRef.delete();

                listData.remove(i);

            }
        }
        Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();

        notifyDataSetChanged();
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
            seller_modify_list_name.setText(" 물건 이름 : "+data.name);
            seller_modify_list_num.setText(" 재고 : "+data.num+" 개");
            seller_modify_list_price.setText(" 가격 : "+data.price+" 원");

            SharedPreferences pref = context.getSharedPreferences("seller", MODE_PRIVATE);
            String seller_ID = pref.getString("id","");

            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + seller_ID + "/" + data.name + ".jpg");
            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(itemView.getContext())
                                .load(task.getResult())
                                .into(seller_modify_list_image);
                    }
                    else
                    {Log.d("SellerRMA", "Glide Error"); }
                }
            });


            if(data.check) {
                seller_modifiy_list_checkbox.setChecked(true);
            }
            else {
                seller_modifiy_list_checkbox.setChecked(false);
            }
            seller_modifiy_list_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        data.check = true;

                    } else {
                        data.check = false;
                    }
                }
            });


        }
    }

    public ArrayList<SellerRMListData> getListData() {
        return listData;
    }

    public void setListData(ArrayList<SellerRMListData> listData) {
        this.listData = listData;
    }
}
