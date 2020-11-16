package com.example.kyngpook.Login_Signup;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignupBuyerActivity1 extends AppCompatActivity implements  View.OnClickListener,Dialog.OnCancelListener{
    EditText authEmail;
    Button authBtn;
    LayoutInflater dialog;
    View dialogLayout;
    Dialog authDialog;
    TextView time_counter; //시간을 보여주는 TextView
    EditText emailAuth_number; //인증 번호를 입력 하는 칸
    EditText NAME;
    Button emailAuth_btn; // 인증버튼
    Button Signup_Next_Btn;
    CountDownTimer countDownTimer;
    String authnumber;
    Spinner major_spinner;
    Spinner year_spinner;
    int state;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private LoadingDialog l;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private boolean email_check;
    private boolean email_rp_check;
    final int MILLISINFUTURE = 300 * 1000; //총 시간 (300초 = 5분)
    final int COUNT_DOWN_INTERVAL = 1000; //onTick 메소드를 호출할 간격 (1초)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        email_check=false;
        email_rp_check=false;
        setContentView(R.layout.activity_signup_buyer1);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());
        Signup_Next_Btn=(Button)findViewById(R.id.signup_next_btn);
        Signup_Next_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(major_spinner.getSelectedItem().toString().equals("학과 선택")){
                    Toast.makeText(SignupBuyerActivity1.this, "학과를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(year_spinner.getSelectedItem().toString().equals("입학년도")){
                    Toast.makeText(SignupBuyerActivity1.this, "입학년도를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(NAME.getText().toString().isEmpty()){
                    Toast.makeText(SignupBuyerActivity1.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(email_rp_check&&email_check){
                    Intent intent=new Intent(getApplicationContext(),SignupBuyerActivity2.class);
                    intent.putExtra("major",major_spinner.getSelectedItem().toString());
                    intent.putExtra("year",year_spinner.getSelectedItem().toString());
                    intent.putExtra("email",authEmail.getText().toString());
                    intent.putExtra("name",NAME.getText().toString());
                    startActivity(intent);
                    customType(SignupBuyerActivity1.this, "left-to-right");
                    finish();
                }
            }
        });
        toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);
        NAME=(EditText)findViewById(R.id.signup_name);
        major_spinner=(Spinner)findViewById(R.id.majorspinner);
        year_spinner=(Spinner)findViewById(R.id.yearspinner);
        authEmail=(EditText)findViewById(R.id.signup_email);
        authBtn=(Button)findViewById(R.id.signup_auth_btn);
        authBtn.setOnClickListener(this);
        authEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email_check=false;
                email_rp_check=false;
                // 입력되는 텍스트에 변화가 있을 때
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //select back button
                finish();
                customType(SignupBuyerActivity1.this, "right-to-left");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void countDownTimer() { //카운트 다운 메소드

        time_counter = (TextView) dialogLayout.findViewById(R.id.emailAuth_time_counter);
        //줄어드는 시간을 나타내는 TextView
        emailAuth_number = (EditText) dialogLayout.findViewById(R.id.emailAuth_number);
        //사용자 인증 번호 입력창
        emailAuth_btn = (Button) dialogLayout.findViewById(R.id.emailAuth_btn);
        //인증하기 버튼


        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) { //(300초에서 1초 마다 계속 줄어듬)

                long emailAuthCount = millisUntilFinished / 1000;
                Log.d("Alex", emailAuthCount + "");

                if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                    time_counter.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                    time_counter.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                }

                //emailAuthCount은 종료까지 남은 시간임. 1분 = 60초 되므로,
                // 분을 나타내기 위해서는 종료까지 남은 총 시간에 60을 나눠주면 그 몫이 분이 된다.
                // 분을 제외하고 남은 초를 나타내기 위해서는, (총 남은 시간 - (분*60) = 남은 초) 로 하면 된다.

            }


            @Override
            public void onFinish() { //시간이 다 되면 다이얼로그 종료

                authDialog.cancel();

            }
        }.start();

        emailAuth_btn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        final String email=authEmail.getText().toString();
        switch(v.getId()){
            case R.id.signup_auth_btn:
                l=new LoadingDialog(SignupBuyerActivity1.this);
                l.setLoadingText("로딩중")
                        .setSuccessText("완료")
                        .setInterceptBack(true)
                        .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                        .show();
                email_rp_check=true;
                Log.d("uid", email);
                email_chk(email);
                break;
            case R.id.emailAuth_btn : //다이얼로그 내의 인증번호 인증 버튼을 눌렀을 시
                String user_answer = emailAuth_number.getText().toString();
                if(user_answer.equals(authnumber)){
                    Toast.makeText(this, "이메일 인증 성공", Toast.LENGTH_SHORT).show();
                    email_check=true;
                    authDialog.cancel();
                }else{
                    Toast.makeText(this, "이메일 인증 실패", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void email_chk(final String Email){
        state=0;
        db.collection("USERS").document("Buyer").collection("Buyer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("userSign", document.getId() + " => " + document.getData());
                                if(document.getData().get("email").toString().equals(Email)) state = 1;
                            }
                        } else {
                            Log.w("userSignError", "Error getting documents.", task.getException());
                        }
                        if(state == 1) {
                            l.close();
                            repeat_email(Email);
                        }
                        else send_message(Email);
                    }
                });

    }
    private void repeat_email(String Email){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(Email+"은 중복된 이메일입니다.");
        builder.setMessage("다른 이메일을 사용하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int btn){
                authEmail.setText(null);
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
    private void send_message(String Email) {
        try {
            GMailSender gMailSender = new GMailSender("testpark0721@gmail.com", "emvwlnmprxqkvunn");
            authnumber = gMailSender.getEmailCode();
            //GMailSender.sendMail(제목, 본문내용, 받는사람);
            gMailSender.sendMail("배달의 그룹 인증 메일입니다.", authnumber, Email);
            l.close();
            Toast.makeText(getApplicationContext(), "인증번호를 해당 메일로 전송하였습니다.", Toast.LENGTH_SHORT).show();
            dialog = LayoutInflater.from(this);
            dialogLayout = dialog.inflate(R.layout.auth_dialog, null); // LayoutInflater를 통해 XML에 정의된 Resource들을 View의 형태로 반환 시켜 줌
            authDialog = new Dialog(this); //Dialog 객체 생성
            authDialog.setContentView(dialogLayout); //Dialog에 inflate한 View를 탑재 하여줌
            authDialog.setCanceledOnTouchOutside(false); //Dialog 바깥 부분을 선택해도 닫히지 않게 설정함.
            authDialog.show(); //Dialog를 나타내어 준다.
            countDownTimer();
        } catch (SendFailedException e) {
            Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        countDownTimer.cancel();
    } //다이얼로그 닫을 때 카운트 다운 타이머의 cancel()메소드 호출
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(SignupBuyerActivity1.this, "right-to-left");
    }
}