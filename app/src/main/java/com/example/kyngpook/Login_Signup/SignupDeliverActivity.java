package com.example.kyngpook.Login_Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class SignupDeliverActivity extends AppCompatActivity {

    private Button button1;
    private  Button button2;
    private  EditText editText1;
    private  EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private String ufid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int state = 1;
    Map<String, Object> deliver = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__deliver);
        button1 = findViewById(R.id.deliverSignUp_button);
        button2 = findViewById(R.id.deliverSignUp_check_button);
        editText1 = findViewById(R.id.deliverSignUp_id);
        editText2 = findViewById(R.id.deliverSignUp_password);
        editText3 = findViewById(R.id.deliverSignUp_passwordC);
        editText4 = findViewById(R.id.deliverSignUp_phone);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dPassword = editText2.getText().toString();
                String dPasswordC = editText3.getText().toString();
                String dPhone = editText4.getText().toString();
                if(ufid == null || !ufid.equals(editText1.getText().toString())) state = 1;
                if(state == 1 )
                    Toast.makeText(getApplicationContext(), "ID 중복확인을 부탁드립니다", Toast.LENGTH_SHORT).show();
                else if(dPassword.length()<8||dPassword.length()>12)
                    Toast.makeText(getApplicationContext(), "비밀번호는 8자 이상 12자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
                else if(!dPassword.equals(dPasswordC))
                    Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                else if(dPhone.length()<10||dPhone.length()>11)
                    Toast.makeText(getApplicationContext(), "전화번호를 제대로 입력해주세요", Toast.LENGTH_SHORT).show();
                else{
                    deliver.put("ID", editText1.getText().toString());
                    deliver.put("PASSWORD", editText2.getText().toString());
                    deliver.put("전화번호", editText4.getText().toString());
                    deliver.put("배달건수","0");
                    db.collection("USERS").document("Deliver").collection("Deliver").document(editText1.getText().toString()).set(deliver);
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),SignupFinishActivity.class);
                    intent.putExtra("ID",editText1.getText().toString());
                    startActivity(intent);
                    customType(SignupDeliverActivity.this, "left-to-right");
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
                    deliverIdCheck(id);
                }
            }
        });
    }
    private void deliverIdCheck(final String did){
        db.collection("USERS").document("Deliver").collection("Deliver")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("deliverSign", document.getId() + " => " + document.getData());
                                if(document.getData().get("ID").toString().equals(did)) state = 1;
                            }
                        } else {
                            Log.w("deliverSignError", "Error getting documents.", task.getException());
                        }
                        if(state == 1) repeat_id(did);
                        else success_id(did);
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
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){

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
        builder.setNeutralButton(null, new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int btn){
                state = 1;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupDeliverActivity.this, "right-to-left");
    }
}
