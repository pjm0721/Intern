package com.example.kyngpook.Login_Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.kyngpook.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignupBuyerActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;
    private EditText editText;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private String ufid;
    private String ufname;
    private Map<String, Object> user = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int state = 1;
    private int stateN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_buyer);

        button1 = findViewById(R.id.userSignUp_button);
        button2 = findViewById(R.id.userSignUpCheck_button1);
        button3 = findViewById(R.id.userSignUpCheck_button2);
        editText = findViewById(R.id.userSignUp_realname);
        editText1 = findViewById(R.id.userSignUp_ID);
        editText2 = findViewById(R.id.userSignUp_name);
        editText3 = findViewById(R.id.userSignUp_password);
        editText4 = findViewById(R.id.userSignUp_pCheck);
        editText5 = findViewById(R.id.userSignUp_phone);

        editText.setText(null);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uID = editText1.getText().toString();
                String uNick = editText2.getText().toString();
                String uRName = editText.getText().toString();
                String uPassword = editText3.getText().toString();
                String uPasswordC = editText4.getText().toString();
                String uPhone = editText5.getText().toString();
                if(ufid == null || !ufid.equals(uID)) state = 1;
                if(ufname == null || !ufname.equals(uNick)) stateN = 1;

                int checking = nameCheck(uRName);
                if(checking==0)
                    Toast.makeText(getApplicationContext(), "이름은 한글이나 영어 하나로만 구성되어야 합니다", Toast.LENGTH_SHORT).show();
                else if(checking==2)
                    Toast.makeText(getApplicationContext(), "한글 성명은 2자 이상 17자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
                else if(checking==3)
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                else if(state == 1 )
                    Toast.makeText(getApplicationContext(), "ID 중복확인을 부탁드립니다", Toast.LENGTH_SHORT).show();
                else if(stateN == 1)
                    Toast.makeText(getApplicationContext(), "닉네임 중복확인을 부탁드립니다", Toast.LENGTH_SHORT).show();
                else if(uPassword.length()<8||uPassword.length()>12)
                    Toast.makeText(getApplicationContext(), "비밀번호는 8자 이상 12자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
                else if(!uPassword.equals(uPasswordC))
                    Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                else if(uPhone.length()<10||uPhone.length()>11)
                    Toast.makeText(getApplicationContext(), "전화번호를 제대로 입력해주세요", Toast.LENGTH_SHORT).show();
                else{
                    user.put("이름",editText.getText().toString());
                    user.put("ID", editText1.getText().toString());
                    user.put("PASSWORD", editText3.getText().toString());
                    user.put("닉네임", editText2.getText().toString());
                    user.put("전화번호", editText5.getText().toString());
                    db.collection("USERS").document("Buyer").collection("Buyer").document(editText1.getText().toString()).set(user);
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity(SignupBuyerActivity.this);
                    Intent intent = new Intent(getApplicationContext(),SignupFinishActivity.class);
                    intent.putExtra("ID",editText1.getText().toString());
                    startActivity(intent);
                    customType(SignupBuyerActivity.this, "left-to-right");
                    finish();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editText1.getText().toString();
                if(id.length()<6||id.length()>10) Toast.makeText(getApplicationContext(),"아이디는 6자 이상 ~ 10자 이하로 입력해주세요.",Toast.LENGTH_SHORT).show();
                else {
                    for(int i=0; i<id.length();i++) {
                        if (('a' <= id.charAt(i) && id.charAt(i) <= 'z') || ('A' <= id.charAt(i) && id.charAt(i) <= 'Z') || ('0' <= id.charAt(i) && id.charAt(i) <= '9'))
                            continue;
                        else {
                            Toast.makeText(getApplicationContext(),"아이디는 영어와 숫자만 입력이 가능합니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    state = 0;
                    userIdCheck(id);
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText2.getText().toString();
                if(name.length()<2||name.length()>10) Toast.makeText(getApplicationContext(),"닉네임 2자 이상 ~ 10자 이하로 입력해주세요.",Toast.LENGTH_SHORT).show();
                else {
                    for(int i=0; i<name.length();i++) {
                        if (('a' <= name.charAt(i) && name.charAt(i) <= 'z') || ('A' <= name.charAt(i) && name.charAt(i) <= 'Z') || ('0' <= name.charAt(i) && name.charAt(i) <= '9')
                                ||('가' <= name.charAt(i)&&name.charAt(i)<='힣'))
                            continue;
                        else {
                            Toast.makeText(getApplicationContext(),"닉네임은 한글, 영어, 숫자만 입력이 가능합니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    stateN = 0;
                    userNameCheck(name);
                }
            }
        });
    }
    private void userIdCheck(final String uid){
        db.collection("USERS").document("Buyer").collection("Buyer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("userSign", document.getId() + " => " + document.getData());
                                if(document.getData().get("ID").toString().equals(uid)) state = 1;
                            }
                        } else {
                            Log.w("userSignError", "Error getting documents.", task.getException());
                        }
                        if(state == 1) repeat_id(uid);
                        else success_id(uid);
                    }
                });
    }
    private void repeat_id(String ID){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(ID+"은 중복된 아이디입니다.");
        builder.setMessage("다른 아이디를 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int btn){
                editText1.setText(null);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn) {
            }
        });
        builder.setNeutralButton(null, new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn){
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
    private void success_id(final String ID){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(ID+"는 사용 가능한 아이디입니다.");
        builder.setMessage("이 아이디를 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int btn){
                ufid = ID;
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn) {
                editText1.setText(null);
                state = 1;
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn){
                state = 1;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void userNameCheck(final String uname){
        db.collection("USERS").document("Buyer").collection("Buyer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("userSign", document.getId() + " => " + document.getData());
                                if(document.getData().get("닉네임").toString().equals(uname)) stateN = 1;
                            }
                        } else {
                            Log.w("userSignError", "Error getting documents.", task.getException());
                        }
                        if(stateN == 1) repeat_name(uname);
                        else success_name(uname);
                    }
                });
    }
    private void repeat_name(String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(name+"은 중복된 닉네임입니다.");
        builder.setMessage("다른 닉네임을 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int btn){
                editText2.setText(null);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn) {
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn){
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
    private void success_name(final String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(name+"는 사용 가능한 닉네임입니다.");
        builder.setMessage("이 닉네임을 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int btn){
                ufname = name;
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn) {
                editText2.setText(null);
                stateN = 1;
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn){
                stateN = 1;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private int nameCheck(String r_name){
        int check = 0;
        if(r_name == null||r_name.equals("")) return 3;
        for(int i=0;i<r_name.length();i++) {
            if ('가' <= r_name.charAt(i) && r_name.charAt(i) <= '힣')
                continue;
            else check = 1;
        }
        if(check == 0 && (r_name.length()<2||r_name.length()>17)) return 2;
        else if(check == 0) return 1;
        check = 0;
        for(int i=0;i<r_name.length();i++) {
            if (('a' <= r_name.charAt(i) && r_name.charAt(i) <= 'z')||('A' <= r_name.charAt(i) && r_name.charAt(i) <= 'Z'))
                continue;
            else check = 1;
        }
        if(check == 0) return 1;
        return 0;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupBuyerActivity.this, "right-to-left");
    }
}
