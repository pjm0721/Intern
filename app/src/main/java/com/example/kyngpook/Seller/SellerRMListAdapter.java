package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/// 판매자 메뉴 관리 어뎁터
public class SellerRMListAdapter extends RecyclerView.Adapter<SellerRMListAdapter.ItemViewHolder> {
    private Context context;

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
        for(int i=listData.size()-1;i>=0;i--)
        {
            if(listData.get(i).check)
                listData.remove(i);
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
        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            seller_modify_list_name = itemView.findViewById(R.id.seller_modify_list_name);
            seller_modify_list_num = itemView.findViewById(R.id.seller_modify_list_num);
            seller_modify_list_price = itemView.findViewById(R.id.seller_modify_list_price);
            seller_modifiy_list_checkbox = itemView.findViewById(R.id.seller_modifiy_list_checkbox);
        }
        //처리하면 댐.
        void onBind(final SellerRMListData data) {
            seller_modify_list_name.setText(" 물건 이름 : "+data.name);
            seller_modify_list_num.setText(" 개수 : "+data.num+" 개");
            seller_modify_list_price.setText(" 가격 : "+data.price+" 원");


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
