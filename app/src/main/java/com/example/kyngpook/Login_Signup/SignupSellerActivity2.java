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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignupSellerActivity2 extends AppCompatActivity {
    EditText id;
    EditText password;
    EditText passwordchk;
    EditText answer;
    private int scs;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Spinner spinner;
    Button fininsh_button;
    Button check_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_seller2);
        Toolbar toolbar = findViewById(R.id.seller_signup_toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);
        id = (EditText) findViewById(R.id.sellerSignUp_id);
        password = (EditText) findViewById(R.id.sellerSignUp_pwd);
        passwordchk = (EditText) findViewById(R.id.sellerSignUp_pwdck);
        spinner = findViewById(R.id.seller_question_spinner);
        answer=(EditText)findViewById(R.id.sellerSignUp_answer);
        check_button=(Button)findViewById(R.id.seller_signup_check_btn);
        fininsh_button=(Button)findViewById(R.id.signup_seller_finish_btn);
        fininsh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ID = id.getText().toString();
                final String PASSWORD = password.getText().toString();
                final String PASSWORD_CHK = passwordchk.getText().toString();
                Intent gintent = getIntent();
                final String NAME = gintent.getStringExtra("NAME");
                final String STORE_NAME = gintent.getStringExtra("STORE_NAME");
                final String STORE_NUM = gintent.getStringExtra("STORE_NUM");
                final String PHONE = gintent.getStringExtra("PHONE");
                idchk(ID);
                if (scs != 2)
                    Toast.makeText(SignupSellerActivity2.this, "중복확인을 부탁드립니다", Toast.LENGTH_SHORT).show();
                else if (PASSWORD.length() < 8 || PASSWORD.length() > 12) {
                    Toast.makeText(SignupSellerActivity2.this, "비밀번호는 8자 이상 12자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
                    password.setText(null);
                } else if (PASSWORD_CHK.equals(PASSWORD) == false) {
                    Toast.makeText(SignupSellerActivity2.this, "비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                    passwordchk.setText(null);
                } else if (queryCheck() == false) {
                    Toast.makeText(getApplicationContext(), "질문을 선택하고 답변을 입력해주세요", Toast.LENGTH_SHORT).show();
                }/*else if(city_first.getSelectedItem().toString().equals("선택해주세요") || city_second.getSelectedItem().toString().equals("선택해주세요") || city_third.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "주소를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
                }*/ else {
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
                    user.put("질문", spinner.getSelectedItem().toString());
                    user.put("답변", answer.getText().toString());
                    user.put("주소","대구광역시 "+gintent.getStringExtra("주소"));
                    db.collection("USERS").document("Seller").collection("Seller").document(ID).set(user);
                    final Map<String,Object> item = new HashMap<>();
                    item.put("상품이름","예시");
                    item.put("개수","1");
                    item.put("가격","1000");


//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
                    db.collection("PRODUCT").document(gintent.getStringExtra("대구광역시")).collection(gintent.getStringExtra("city_first")).
                            document(ID).collection("판매상품").document("예시").set(item);
//
//                        }
//                    }, 1000);


                    ActivityCompat.finishAffinity(SignupSellerActivity2.this);
                    Intent intent=new Intent(getApplicationContext(),SignupFinishActivity.class);
                    intent.putExtra("ID",ID);
                    startActivity(intent);
                    customType(SignupSellerActivity2.this,"left-to-right");
                    finish();
                    /*  user.put("주소",city_first.getSelectedItem().toString()+" "+city_second.getSelectedItem().toString()+" "+city_third.getText().toString());*/
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //select back button
                finish();
                customType(SignupSellerActivity2.this, "right-to-left");
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if(answer.getText().toString().equals("")) return false;
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupSellerActivity2.this, "right-to-left");

    }
}

