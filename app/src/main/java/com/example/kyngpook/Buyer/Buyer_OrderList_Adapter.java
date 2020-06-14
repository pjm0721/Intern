package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_OrderList_Adapter extends RecyclerView.Adapter<Buyer_OrderList_Adapter.ItemViewHolder> {
    private Buyer_MainActivity context;

    private ArrayList<orderInfo> listData = new ArrayList<>();

    public Buyer_OrderList_Adapter(Context context) {
        this.context = (Buyer_MainActivity) context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_orderlistactivity_item, parent, false);
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

    public void addItem(orderInfo data) {
        listData.add(data);
        notifyDataSetChanged();
    }

    public void clear() {
        listData.clear();
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView storeText;
        private TextView timeText;
        private TextView stateText;
        private TextView addressText;
        private TextView orderText;
        private TextView priceText;
        private Button reviewBtn;

        ItemViewHolder(View itemView) {
            super(itemView);
            storeText = itemView.findViewById(R.id.Buyer_OrderList_item_storeNameText);
            timeText = itemView.findViewById(R.id.Buyer_OrderList_item_timeText);
            stateText = itemView.findViewById(R.id.Buyer_OrderList_item_orderStateText);
            addressText = itemView.findViewById(R.id.Buyer_OrderList_item_addressText);
            orderText = itemView.findViewById(R.id.Buyer_OrderList_item_orderListText);
            priceText = itemView.findViewById(R.id.Buyer_OrderList_item_priceText);
            reviewBtn = itemView.findViewById(R.id.Buyer_OrderList_item_reviewBtn);
        }

        void onBind(final orderInfo data) {
            storeText.setText(data.store);
            timeText.setText("주문시간 : " + data.time);
            stateText.setText("주문상태 : " + data.state);
            addressText.setText("주문주소 : "+data.address);
            orderText.setText("주문내역 : "+data.order);

            DecimalFormat formatter = new DecimalFormat("###,###");
            priceText.setText("금액 : " + formatter.format(data.price) + " 원");
            reviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!data.review) {
                        CustomDialog mCustomDialog = new CustomDialog(context, data);
                        mCustomDialog.show();
                        Display display = context.getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        Window window = mCustomDialog.getWindow();
                        int x = (int)(size.x * 0.8f);
                        int y = (int)(size.y * 0.5f);
                        window.setLayout(x, y);
                    }
                    else
                        Toast.makeText(context, "이미 리뷰를 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public ArrayList<orderInfo> getListData() {
        return listData;
    }

    public void setListData(ArrayList<orderInfo> listData) {
        this.listData = listData;
    }


    class CustomDialog extends Dialog {

        private Button Btn1, Btn2, Btn3, Btn4, Btn5, okBtn;
        private EditText contentEdit;
        private int score = 0;
        private orderInfo info;

        public CustomDialog(@NonNull Context context, orderInfo info) {
            super(context);
            this.info = info;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            setContentView(R.layout.buyer_orderlist_dialog);

            Btn1 = (Button) findViewById(R.id.Buyer_OrderList_dialog_Btn1);
            Btn2 = (Button) findViewById(R.id.Buyer_OrderList_dialog_Btn2);
            Btn3 = (Button) findViewById(R.id.Buyer_OrderList_dialog_Btn3);
            Btn4 = (Button) findViewById(R.id.Buyer_OrderList_dialog_Btn4);
            Btn5 = (Button) findViewById(R.id.Buyer_OrderList_dialog_Btn5);
            okBtn = (Button) findViewById(R.id.Buyer_OrderList_dialog_okBtn);
            contentEdit = (EditText) findViewById(R.id.Buyer_OrderList_dialog_EditText);

            btnInit();

            // 클릭 이벤트 셋팅
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(score == 0)
                        Toast.makeText(context, "평점을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    else if(contentEdit.getText().length() < 10)
                        Toast.makeText(context, "리뷰는 최소 10자 이상 부탁드립니다.", Toast.LENGTH_SHORT).show();
                    else {
                        Map<String, Object> rv = new HashMap<>();
                        rv.put("내용", contentEdit.getText().toString());
                        rv.put("평점", score);
                        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy년 MM월dd일 HH:mm");
                        Date time = new Date();
                        String time2 = format2.format(time);
                        rv.put("시간", time2);

                        SharedPreferenceUtil util = new SharedPreferenceUtil(context);
                        String nickname = util.getStringData("닉네임", "조선왕조실룩샐룩");
                        rv.put("닉네임", nickname);
                        context.addReviewDb1(rv, info);
                        CustomDialog.this.dismiss();
                    }
                }
            });
            Btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    score = 1;
                    btnInit();
                    Btn1.setBackgroundColor(Color.BLUE); Btn1.setTextColor(Color.YELLOW);
                }
            });
            Btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    score = 2;
                    btnInit();
                    Btn2.setBackgroundColor(Color.BLUE); Btn2.setTextColor(Color.YELLOW);
                }
            });
            Btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    score = 3;
                    btnInit();
                    Btn3.setBackgroundColor(Color.BLUE); Btn3.setTextColor(Color.YELLOW);
                }
            });
            Btn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    score = 4;
                    btnInit();
                    Btn4.setBackgroundColor(Color.BLUE); Btn4.setTextColor(Color.YELLOW);
                }
            });
            Btn5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    score = 5;
                    btnInit();
                    Btn5.setBackgroundColor(Color.BLUE); Btn5.setTextColor(Color.YELLOW);
                }
            });
        }
        private void btnInit() {
            Btn1.setBackgroundColor(Color.YELLOW); Btn1.setTextColor(Color.BLUE);
            Btn2.setBackgroundColor(Color.YELLOW); Btn2.setTextColor(Color.BLUE);
            Btn3.setBackgroundColor(Color.YELLOW); Btn3.setTextColor(Color.BLUE);
            Btn4.setBackgroundColor(Color.YELLOW); Btn4.setTextColor(Color.BLUE);
            Btn5.setBackgroundColor(Color.YELLOW); Btn5.setTextColor(Color.BLUE);
        }
    }
}