package com.example.kyngpook.Login_Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignupSellerActivity extends AppCompatActivity {
    private TextView phonenumber;
    private TextView storename;
    private TextView storenumber;
    private TextView name;
    private Toast toast;
    private EditText editText;
    private Spinner city_first;
    private Spinner city_second;
    private EditText city_third;
    private Button seller_next_btn;
    private Button seller_address_btn;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    ArrayAdapter<CharSequence> adspin;
    private TextView et_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_seller1);
        Toolbar toolbar = findViewById(R.id.seller_signup_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);
        CollectionReference area=db.collection("지역");
/*        String[] arr1=new String[1]; arr1[0]="동/면/읍 선택";
        ArrayAdapter<String> sp_adapter_city2=new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, arr1);

        city_second.setAdapter(sp_adapter_city2);*/

        area.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            String[] city_arr=new String[100];
                            int a=0;
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                city_arr[++a]=doc.getId();
                            }
                            String[] arr=new String[++a];

                            arr[0]="구/군 선택";
                            for(int i=1;i<a;i++)
                                arr[i]=city_arr[i];

                            ArrayAdapter<String> sp_adapter_city=new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, arr);

                            city_first.setAdapter(sp_adapter_city);
                            String[] arr2=new String[1];
                            arr2[0]="동/면/읍 선택";
                            ArrayAdapter<String> sp_adapter_city2=new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, arr2);

                            city_second.setAdapter(sp_adapter_city2);

                        } else {
                            Log.w("signUpSeller", "Error", task.getException());
                        }
                    }
                });
        city_first=(Spinner)findViewById(R.id.city_first);
        city_second=(Spinner)findViewById(R.id.city_second);
        city_third=(EditText)findViewById(R.id.seller_detail_address);
        phonenumber = (TextView) findViewById(R.id.sellerSignUp_phone);
        name = (TextView) findViewById(R.id.sellerSignUp_name);
        storename = (TextView) findViewById(R.id.sellerSignUp_store_name);
        storenumber = (TextView) findViewById(R.id.sellerSignUp_store_num);

        seller_next_btn=(Button)findViewById(R.id.signup_next_btn);
        seller_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String PHONE = phonenumber.getText().toString();
                final String NAME = name.getText().toString();
                final String STORE_NAME = storename.getText().toString();
                final String STORE_NUM = storenumber.getText().toString();
                if (TextUtils.isEmpty(NAME) == true)
                    Toast.makeText(SignupSellerActivity.this, "대표자명을 입력해주세요", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(STORE_NAME) == true)
                    Toast.makeText(SignupSellerActivity.this, "업소명을 입력해주세요", Toast.LENGTH_SHORT).show();
                else if (STORE_NUM.length() != 10) {
                    Toast.makeText(SignupSellerActivity.this, "사업자번호 10자리를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (PHONE.length() != 10 && PHONE.length() != 11) {
                    Toast.makeText(SignupSellerActivity.this, "전화번호를 제대로 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(city_first.getSelectedItem().toString().equals("구/군 선택") || city_second.getSelectedItem().toString().equals("동/면/읍 선택") || city_third.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "주소를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent(getApplicationContext(),SignupSellerActivity2.class);
                    intent.putExtra("NAME",NAME);
                    intent.putExtra("STORE_NAME",STORE_NAME);
                    intent.putExtra("STORE_NUM",STORE_NUM);
                    intent.putExtra("PHONE",PHONE);
                    intent.putExtra("city_first",city_first.getSelectedItem().toString());
                    intent.putExtra("city_second",city_second.getSelectedItem().toString());
                    intent.putExtra("주소",city_first.getSelectedItem().toString()+" "+city_second.getSelectedItem().toString()+" "+city_third.getText().toString());
                    startActivity(intent);
                    customType(SignupSellerActivity.this,"left-to-right");
                }
            }
        });
        city_first.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String city=parent.getItemAtPosition(position).toString();
                if(!city.equals("구/군 선택"))
                    show_second_city(city);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void show_second_city(String city)
    {
        DocumentReference second_city=db.collection("지역").document(city);

        second_city.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {
                    int num = Integer.parseInt(task.getResult().get("개수").toString());

                    String[] arr = new String[++num];

                    arr[0]="동/면/읍 선택";
                    for (int i = 1; i < num; i++) {
                        arr[i] = task.getResult().get("" + i).toString();
                    }

                    ArrayAdapter<String> sp_adapter_area = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, arr);

                    city_second.setAdapter(sp_adapter_area);

                }
            }
        });
    }
 public void onActivityResult(int requestCode, int resultCode, Intent intent)
 {
     super.onActivityResult(requestCode, resultCode, intent);
     switch (requestCode) {
         case SEARCH_ADDRESS_ACTIVITY:
             if (resultCode == RESULT_OK) {
                 String data = intent.getExtras().getString("data");
                 if (data != null) {
                     et_address.setText(data);
                 }
             }
             break;
     }
 }
 @Override
 public boolean onOptionsItemSelected(@NonNull MenuItem item) {
     switch (item.getItemId()){
         case android.R.id.home:
             //select back button
             finish();
             customType(SignupSellerActivity.this, "right-to-left");
             break;
     }
     return super.onOptionsItemSelected(item);
 }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupSellerActivity.this, "right-to-left");

    }
}

