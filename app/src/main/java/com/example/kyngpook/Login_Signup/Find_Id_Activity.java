package com.example.kyngpook.Login_Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static maes.tech.intentanim.CustomIntent.customType;

public class Find_Id_Activity extends AppCompatActivity {

    private String find_Id;
    private Button find_id_btn;
    private RadioGroup find_id_rgp;
    private RadioButton find_id_rb;
    private EditText find_name;
    private EditText find_number;
    private int state = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        find_id_btn = findViewById(R.id.findId_button);
        find_id_rgp = findViewById(R.id.findId_rgp);
        find_name = findViewById(R.id.findId_realname);
        find_number = findViewById(R.id.findId_phone);
        find_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findId();
            }
        });
    }
    private void findId()
    {
        final String nametext=find_name.getText().toString();
        final String numbertext=find_number.getText().toString();
        if(nametext.isEmpty()==true||numbertext.isEmpty()==true){
            Toast.makeText(this, "이름과 전화번호를 입력해주세요", Toast.LENGTH_LONG).show();
            return;
        }
        int id = find_id_rgp.getCheckedRadioButtonId();
        find_id_rb = (RadioButton)findViewById(id);
        final String who= find_id_rb.getText().toString();
        String user=null;
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
        else name = null;
        db.collection("USERS").document(user).collection(user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("find_id", document.getId() + " => " + document.getData());
                                if(document.getData().get(name).toString().equals(nametext)==true) {
                                    if (document.getData().get("전화번호").toString().equals(numbertext)==true){
                                        find_Id = document.getId();
                                        state = 1;
                                    }
                                };
                            }
                        } else {
                             Log.w("find_id", "Error getting documents.", task.getException());
                        }
                        if(state==1)
                        {
                            Intent intent = new Intent(getApplicationContext(),Find_Id_Complete.class);
                            intent.putExtra("ID",find_Id);
                            startActivity(intent);
                            customType(Find_Id_Activity.this, "left-to-right");
                            finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "입력 정보에 해당하는 계정이 없습니다", Toast.LENGTH_LONG).show();
                    }

                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Find_Id_Activity.this, "right-to-left");
    }
}
