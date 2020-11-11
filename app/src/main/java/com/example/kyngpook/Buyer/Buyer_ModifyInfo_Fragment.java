package com.example.kyngpook.Buyer;

import com.example.kyngpook.Login_Signup.Find_Id_Complete;
import com.example.kyngpook.Login_Signup.LogInActivity;
import com.example.kyngpook.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import static maes.tech.intentanim.CustomIntent.customType;

public class Buyer_ModifyInfo_Fragment  extends Fragment {
    Buyer_MainActivity activity;
    ViewGroup rootView;
    private SharedPreferenceUtil util;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Buyer_MainActivity)getActivity();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.activity_buyer__modify_info, container, false);
        util = new SharedPreferenceUtil(activity);
        Button logout_btn=rootView.findViewById(R.id.Buyer_logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Buyer_Logout();
            }
        });
        TextView idText = rootView.findViewById(R.id.Buyer_ModifyInfoActivity_idText);
        idText.setText(util.getStringData("ID", "id1.default"));
        final EditText nameEdit = rootView.findViewById(R.id.Buyer_ModifyInfoActivity_nameEdit);
        nameEdit.setText(util.getStringData("이름", "이름.default"));
        final EditText nicknameEdit = rootView.findViewById(R.id.Buyer_ModifyInfoActivity_nicknameEdit);
        nicknameEdit.setText(util.getStringData("닉네임", "닉네임.default"));
        final EditText callEdit = rootView.findViewById(R.id.Buyer_ModifyInfoActivity_callEdit);
        callEdit.setText(util.getStringData("전화번호", "01012341234.default"));

        final CheckBox checkBox = rootView.findViewById(R.id.Buyer_ModifyInfoActivity_checkBox);
        final EditText pwEdit = rootView.findViewById(R.id.Buyer_ModifyInfoActivity_pwEdit);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttnView, boolean isChecked) {
                if (isChecked) {
                    pwEdit.setFocusable(true);
                    pwEdit.setFocusableInTouchMode(true);
                    pwEdit.setCursorVisible(true);
                    pwEdit.setEnabled(true);
                } else {
                    pwEdit.setFocusable(false);
                    pwEdit.setFocusableInTouchMode(false);
                    pwEdit.setCursorVisible(false);
                    pwEdit.setEnabled(false);
                    pwEdit.setText(null);
                }

            }
        });
        rootView.findViewById(R.id.Buyer_ModifyInfoActivity_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = util.getStringData("PASSWORD", "1234");
                if(checkBox.isChecked()) {
                    password = pwEdit.getText().toString();
                    if(password.length() < 7) {
                        Toast.makeText(activity, "비밀번호가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        Log.d("HI", String.valueOf(checkBox.isChecked()) + " -> " + password);
                        util.setStringData("PASSWORD", password);
                    }
                }
                String nickname = nicknameEdit.getText().toString();
                if(nickname.length() < 2) {
                    Toast.makeText(activity, "닉네임이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                util.setStringData("닉네임", nickname);

                String name = nameEdit.getText().toString();
                if(name.length() < 2) {
                    Toast.makeText(activity, "이름이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                util.setStringData("이름", name);

                String call = callEdit.getText().toString();
                if(call.contains("-")) {
                    Toast.makeText(activity, "'-'는 생략하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                util.setStringData("전화번호", call);
                Map<String, Object> info = new HashMap<>();
                pwEdit.clearFocus();
                nameEdit.clearFocus();
                nicknameEdit.clearFocus();
                callEdit.clearFocus();
                String id = util.getStringData("ID", "id1");
                info.put("ID", id);
                info.put("PASSWORD", password);
                info.put("닉네임", nickname);
                info.put("이름", name);
                info.put("전화번호", call);

                db.collection("USERS").document("Buyer").collection("Buyer")
                        .document(id)
                        .set(info, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d("Buyer_ModifyInfoActivity", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.w("Buyer_ModifyInfoActivity", "Error writing document", e);
                            }
                        });
                Toast.makeText(activity, "완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;

    }

}