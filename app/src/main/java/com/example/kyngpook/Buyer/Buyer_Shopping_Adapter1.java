package com.example.kyngpook.Buyer;

import com.bumptech.glide.Glide;
import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_Shopping_Adapter1 extends RecyclerView.Adapter<Buyer_Shopping_Adapter1.ItemViewHolder> {
    private Buyer_ShoppingActivity context;
    private String SellerID;
    private ArrayList<ITEM> listData = new ArrayList<>();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;


    public Buyer_Shopping_Adapter1(Context context, String SellerID) {
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
        private Button numberText;
        private TextView priceText;
        private LinearLayout addBtn;
        private ImageView imageView;

        //ID다 찾아주고
        ItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.Buyer_Shopping_item_nameText);
            numberText = itemView.findViewById(R.id.Buyer_Shopping_item_numText);
            priceText = itemView.findViewById(R.id.Buyer_Shopping_item_priceText);
            addBtn = itemView.findViewById(R.id.Buyer_Shopping_item_addBtn1);
            imageView = itemView.findViewById(R.id.Buyer_Shopping_item_imgView);
        }

        //처리하면 댐.
        void onBind(final ITEM data) {
            nameText.setText(data.name);
            numberText.setText("수량 : " + data.number);
            DecimalFormat formatter = new DecimalFormat("###,###");
            priceText.setText(formatter.format(Integer.valueOf(data.price)) + " 원");

            numberText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.addItemToTempBasket(data);
                }
            });
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.addItemToTempBasket(data);
                }
            });
            storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + SellerID + "/" + data.name + ".jpg");

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog mCustomDialog = new CustomDialog(context, data);
                    mCustomDialog.show();
//                    Display display = context.getWindowManager().getDefaultDisplay();
//                    Point size = new Point();
//                    display.getSize(size);
//                    Window window = mCustomDialog.getWindow();
//                    int x = (int) (size.x * 0.9f);
//                    int y = (int) (size.y * 0.5f);
//                    window.setLayout(x, y);
                }
            });

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

    class CustomDialog extends Dialog {

        private Button okBtn;
        private ImageView imgView;
        private ITEM item;

        public CustomDialog(@NonNull Context context, ITEM item) {
            super(context);
            this.item = item;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            setContentView(R.layout.buyer_shopping_dialog);

            okBtn = (Button) findViewById(R.id.Buyer_Shopping_dialog_okBtn);
            imgView = (ImageView) findViewById(R.id.Buyer_Shopping_dialog_ImageView);

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Buyer_Shopping_Adapter1.CustomDialog.this.dismiss();
                }
            });

            storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + SellerID + "/" + item.name + ".jpg");
            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(context)
                                .load(task.getResult())
                                .into(imgView);
                    } else {
//                        Log.d("Buyer_MarketList_Adapter", "Glide Error");
                    }
                }
            });
        }
    }
}




