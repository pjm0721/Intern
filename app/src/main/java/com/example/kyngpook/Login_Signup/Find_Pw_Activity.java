package com.example.kyngpook.Login_Signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static maes.tech.intentanim.CustomIntent.customType;

public class Find_Pw_Activity extends AppCompatActivity {

    private String pfind_Pw;
    private Button pfind_id_btn;
    private RadioGroup pfind_id_rgp;
    private RadioButton pfind_id_rb;
    private EditText pfind_name;
    private EditText pfind_number;
    private EditText pfind_id;
    private EditText pfind_answer;
    private Spinner spinner;
    private int state = 0;
    private  FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        pfind_id_btn = findViewById(R.id.findPw_button);
        pfind_id_rgp = findViewById(R.id.findPw_rgp);
        pfind_name = findViewById(R.id.findPw_realname);
        pfind_number = findViewById(R.id.findPw_phone);
        pfind_id = findViewById(R.id.findPw_Id);
        pfind_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPw();
            }
        });
        pfind_answer = findViewById(R.id.findPw_answer);
        spinner = findViewById(R.id.findPw_spinner);

        final String[] qr=new String[]{"질문을 선택해주세요.","나의 보물 1호는?","어머니 성함은?","아버지 성함은?",
                "나의 어릴적 별명은?","출신 초등학교 이름은?","내가 태어난 지역은?","첫 사랑 이름은?"};

        ArrayAdapter<String> sp_adapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, qr);

        spinner.setAdapter(sp_adapter);
    }
    private void findPw()
    {
        final String nametext=pfind_name.getText().toString();
        final String numbertext=pfind_number.getText().toString();
        final String idtext=pfind_id.getText().toString();
        final String qtext=spinner.getSelectedItem().toString();
        final String atext=pfind_answer.getText().toString();
        if(nametext.isEmpty()==true||numbertext.isEmpty()==true||idtext.isEmpty()==true){
            Toast.makeText(this, "이름과 아이디, 전화번호를 입력해주세요", Toast.LENGTH_LONG).show();
            return;
        }
        int id = pfind_id_rgp.getCheckedRadioButtonId();
        pfind_id_rb = (RadioButton)findViewById(id);
        final String who= pfind_id_rb.getText().toString();
        final String user;
        final String name;
        if(who.equals("일반 이용자")==true) {
            user="Buyer";
            name="이름";
        }
        else if(who.equals("판매자")==true) {
            user="Seller";
            name="대표자명";
        }
        else if(who.equals("배달원")==true) {
            user = "Deliver";
            name = "이름";
        }
        else {
            name = null; user = null;
        }
        db.collection("USERS").document(user).collection(user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("find_id", document.getId() + " => " + document.getData());
                                if (document.getData().get(name).toString().equals(nametext) == true) {
                                    if (document.getData().get("전화번호").toString().equals(numbertext) == true) {
                                        if (document.getData().get("ID").toString().equals(idtext) == true) {
                                            if (document.getData().get("질문").toString().equals(qtext) && document.getData().get("답변").toString().equals(atext)) {
                                                pfind_Pw = document.getData().get("ID").toString();
                                                state = 1;
                                            }
                                        }
                                    }
                                }
                                ;
                            }
                        } else {
                            Log.w("find_id", "Error getting documents.", task.getException());
                        }
                        if (state == 1) {
                            Intent intent = new Intent(getApplicationContext(), Find_Pw_Complete.class);
                            intent.putExtra("ID", pfind_Pw);
                            intent.putExtra("USER",user);
                            startActivity(intent);
                            customType(Find_Pw_Activity.this, "left-to-right");
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "입력 정보에 해당하는 계정이 없습니다", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "정보를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Find_Pw_Activity.this, "right-to-left");
    }
}
