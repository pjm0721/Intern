package com.example.kyngpook.Seller;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// 판매자 가게 정보 수정 시 메뉴 추가 시 Alert형태로 뜨는 액티비티
public class SellerPlusItem extends AppCompatActivity {

    public static final int IMAGE_REQUEST = 1000;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView seller_add_image;
    private Bitmap bitmap;
    LoadingDialog l;

    TextView seller_add_name;
    TextView seller_add_num;
    TextView seller_add_price;
    byte[] byte_data;
    SharedPreferences pref;
    String seller_ID;
   // int sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_plus_item);

        seller_add_name = findViewById(R.id.seller_add_name);
        seller_add_num = findViewById(R.id.seller_add_num);
        seller_add_price = findViewById(R.id.seller_add_price);
        seller_add_image = findViewById(R.id.seller_add_image);

        Button seller_add_ok = findViewById(R.id.seller_add_ok);
        Button seller_add_cancle = findViewById(R.id.seller_add_cancle);

        storage = FirebaseStorage.getInstance();

        seller_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seller_add_name.getText().toString().length()!=0) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_REQUEST);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"상품 이름을 먼저 등록해주세요",Toast.LENGTH_LONG).show();
                }
            }
        });

        // 확인 버튼 누르면 입력 데이터를 넘겨준다
        seller_add_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String name = seller_add_name.getText().toString();
                String num = seller_add_num.getText().toString();
                String price = seller_add_price.getText().toString();

                if (name.length() != 0 && num.length() != 0 && price.length() != 0 && bitmap!=null) {
                    intent.putExtra("상품이름", name);
                    intent.putExtra("개수", num);
                    intent.putExtra("가격", price);


                    setResult(RESULT_OK, intent);

                    UploadTask uploadTask= (UploadTask) storageRef.putBytes(byte_data);
                    finish();

                } else {
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(getApplicationContext(), "모두 입력해주세요", Toast.LENGTH_LONG).show();
                }

            }
        });

        // 취소 버튼 누르면 그냥 닫긴다
        seller_add_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST)
        {
            if(resultCode==RESULT_OK)
            {
                try {

                    String name = seller_add_name.getText().toString();
                    InputStream in=getContentResolver().openInputStream(data.getData());

                    bitmap= BitmapFactory.decodeStream(in);
                    in.close();

                    SharedPreferences pref = getSharedPreferences("seller", MODE_PRIVATE);
                    final String seller_ID = pref.getString("id","");

                    seller_add_image.setDrawingCacheEnabled(true);
                    seller_add_image.buildDrawingCache();

                    storageRef = storage.getReferenceFromUrl("gs://internproject-2e699.appspot.com/seller/" + seller_ID + "/" + name + ".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte_data = baos.toByteArray();

                    seller_add_image.setImageBitmap(bitmap);
                }
                catch (Exception e){
                    Toast.makeText(this,"사진 선택 에러",Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this,"사진 선택 취소",Toast.LENGTH_LONG).show();
            }
        }

    }


}

