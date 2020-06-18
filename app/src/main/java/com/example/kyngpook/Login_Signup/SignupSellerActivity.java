package com.example.kyngpook.Login_Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private TextView id;
    private TextView password;
    private TextView passwordchk;
    private TextView phonenumber;
    private TextView storename;
    private TextView storenumber;
    private TextView name;
    private Toast toast;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int scs = 0;
    private EditText editText;
    private Spinner spinner;
    private Spinner city_first;
    private Spinner city_second;
    private EditText city_third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_seller);
        id = (TextView) findViewById(R.id.sellerSignUp_id);
        password = (TextView) findViewById(R.id.sellerSignUp_pwd);
        passwordchk = (TextView) findViewById(R.id.sellerSignUp_pwdck);
        phonenumber = (TextView) findViewById(R.id.sellerSignUp_phone);
        name = (TextView) findViewById(R.id.sellerSignUp_name);
        storename = (TextView) findViewById(R.id.sellerSignUp_store_name);
        storenumber = (TextView) findViewById(R.id.sellerSignUp_store_num);
        editText = findViewById(R.id.sellerSignUp_answer);
        spinner = findViewById(R.id.sellerSignUp_spinner);

        final String[] qr=new String[]{"질문을 선택해주세요.","나의 보물 1호는?","어머니 성함은?","아버지 성함은?",
                "나의 어릴적 별명은?","출신 초등학교 이름은?","내가 태어난 지역은?","첫 사랑 이름은?"};

        ArrayAdapter<String> sp_adapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, qr);

        spinner.setAdapter(sp_adapter);
        city_first=findViewById(R.id.city_first);
        city_second=findViewById(R.id.city_second);
        city_third=findViewById(R.id.city_third);

        CollectionReference area=db.collection("지역");

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

                            arr[0]="선택해주세요";
                            for(int i=1;i<a;i++)
                                arr[i]=city_arr[i];

                            ArrayAdapter<String> sp_adapter_city=new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, arr);

                            city_first.setAdapter(sp_adapter_city);

                        } else {
                            Log.w("signUpSeller", "Error", task.getException());
                        }
                    }
                });


        city_first.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String city=parent.getItemAtPosition(position).toString();
                if(!city.equals("선택해주세요"))
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

                    arr[0]="선택해주세요";
                    for (int i = 1; i < num; i++) {
                        arr[i] = task.getResult().get("" + i).toString();
                    }

                    ArrayAdapter<String> sp_adapter_area = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, arr);

                    city_second.setAdapter(sp_adapter_area);

                }
            }
        });
    }

    public void on_seller_idchk(View v) {
        final String ID = id.getText().toString();
        idchk(ID);
        if (scs == 1) repeat_id(ID);
        else if (scs == 2) success_id(ID);
    }

    public void idchk(String ID) {
        scs = 0;
        String pattern = "^[a-zA-Z가-힣0-9]{3,10}$";
        if (ID.length() < 3 || ID.length() > 10) {
            Toast.makeText(this, "아이디는 3자 이상 10자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Pattern.matches(pattern, ID) == false) {
            Toast.makeText(this, "사용할 수 없는 아이디 형식입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("USERS").document("Seller").collection("Seller")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LoginActivity.java", document.getId() + " => " + document.getData());
                                if (document.getId().equals(id.getText().toString()) == true) {
                                    scs = 1;
                                }
                            }
                        } else {
//                             Log.w("LoginActivity.java", "Error getting documents.", task.getException());
                        }
                    }

                });
        if (scs == 0) scs = 2;
    }

    public void on_seller_signup(View v) {
        final String ID = id.getText().toString();
        final String PASSWORD = password.getText().toString();
        final String PASSWORD_CHK = passwordchk.getText().toString();
        final String PHONE = phonenumber.getText().toString();
        final String NAME = name.getText().toString();
        final String STORE_NAME = storename.getText().toString();
        final String STORE_NUM = storenumber.getText().toString();
        idchk(ID);
        if (scs != 2) Toast.makeText(this, "중복확인을 부탁드립니다", Toast.LENGTH_SHORT).show();
        else if (PASSWORD.length() < 8 || PASSWORD.length() > 12) {
            Toast.makeText(this, "비밀번호는 8자 이상 12자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
            password.setText(null);
        } else if (PASSWORD_CHK.equals(PASSWORD) == false) {
            Toast.makeText(this, "비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
            passwordchk.setText(null);
        } else if (TextUtils.isEmpty(NAME) == true)
            Toast.makeText(this, "대표자명을 입력해주세요", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(STORE_NAME) == true)
            Toast.makeText(this, "업소명을 입력해주세요", Toast.LENGTH_SHORT).show();
        else if (STORE_NUM.length() != 10) {
            Toast.makeText(this, "사업자번호 10자리를 입력해주세요", Toast.LENGTH_SHORT).show();
            password.setText(null);
        } else if (PHONE.length() != 10 && PHONE.length() != 11) {
            Toast.makeText(this, "전화번호를 제대로 입력해주세요", Toast.LENGTH_SHORT).show();
            password.setText(null);
        } else if (queryCheck()==false) {
            Toast.makeText(getApplicationContext(), "질문을 선택하고 답변을 입력해주세요", Toast.LENGTH_SHORT).show();
        }else if(city_first.getSelectedItem().toString().equals("선택해주세요") || city_second.getSelectedItem().toString().equals("선택해주세요") || city_third.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "주소를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else {
            Map<String, Object> user = new HashMap<>();
            user.put("ID", ID);
            user.put("PASSWORD", PASSWORD);
            user.put("대표자명", NAME);
            user.put("업소명", STORE_NAME);
            user.put("사업자번호", STORE_NUM);
            user.put("전화번호", PHONE);
            user.put("영업시간", "");
            user.put("주소", "");
            user.put("카테고리", "");
            user.put("휴무일", "");
            user.put("권한", 0);
            user.put("리뷰고유값", 0);
            user.put("질문",spinner.getSelectedItem().toString());
            user.put("답변",editText.getText().toString());
            user.put("주소",city_first.getSelectedItem().toString()+" "+city_second.getSelectedItem().toString()+" "+city_third.getText().toString());

            db.collection("USERS").document("Seller").collection("Seller").document(ID).set(user);

            Map<String,Object> item = new HashMap<>();
            item.put("상품이름","예시");
            item.put("개수","1");
            item.put("가격","1000");

            db.collection("PRODUCT").document(city_first.getSelectedItem().toString()).collection(city_second.getSelectedItem().toString()).
                    document(ID).collection("판매상품").document("").set(item);

            ActivityCompat.finishAffinity(SignupSellerActivity.this);
            Intent intent = new Intent(getApplicationContext(), SignupFinishActivity.class);
            intent.putExtra("ID", ID);
            customType(SignupSellerActivity.this, "left-to-right");
            startActivity(intent);
        }
    }

    private void repeat_id(String ID) {
        scs = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(ID + "은 중복된 아이디입니다.");
        builder.setMessage("다른 아이디를 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int btn) {
                id.setText(null);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int btn) {
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int btn) {
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void success_id(String ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(ID + "는 사용 가능한 아이디입니다.");
        builder.setMessage("이 아이디를 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int btn) {
                scs = 2;
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int btn) {
                id.setText(null);
            }
        });
        builder.setNeutralButton(null, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int btn) {
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
    private boolean queryCheck()
    {
        if(spinner.getSelectedItem().toString().equals("질문을 선택해주세요.")) return false;
        if(editText.getText().toString().equals("")) return false;
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupSellerActivity.this, "right-to-left");

    }
}