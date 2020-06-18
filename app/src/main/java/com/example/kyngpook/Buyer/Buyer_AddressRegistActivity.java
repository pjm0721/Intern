package com.example.kyngpook.Buyer;

import com.example.kyngpook.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Buyer_AddressRegistActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String address1 = "";
    private String address2 = "";
    private GpsTracker gpsTracker;
    private ViewGroup gps_btn;
    private ImageView gps_img;
    private TextView gps_address;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    private SharedPreferenceUtil util;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Buyer_Address_Busan_Fragment fragment2;
    private Buyer_Address_Daegu_Fragment fragment1;
    private Button busan;
    private Button daegu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer__address_regist);
        final EditText editText = (EditText) findViewById(R.id.Buyer_AddressRegistActivity_EditText);

        util = new SharedPreferenceUtil(this);

        fragment1 = new Buyer_Address_Daegu_Fragment();
        fragment2 = new Buyer_Address_Busan_Fragment();
        fragmentManager.beginTransaction().add(R.id.Buyer_AddressRegistActivity_Container, fragment1).hide(fragment1).commit();
        fragmentManager.beginTransaction().add(R.id.Buyer_AddressRegistActivity_Container, fragment2).hide(fragment2).commit();
        busan = (Button) findViewById(R.id.Buyer_AddressRegistActivity_BusanBtn);
        daegu = (Button) findViewById(R.id.Buyer_AddressRegistActivity_DaeguBtn);

        daegu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().hide(fragment2).commit();
                fragmentManager.beginTransaction().show(fragment1).commit();
                daegu.setBackgroundResource(R.drawable.buyer_button_shape3);
                busan.setBackgroundResource(R.drawable.buyer_button_shape);
            }
        });
        busan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().hide(fragment1).commit();
                fragmentManager.beginTransaction().show(fragment2).commit();
                daegu.setBackgroundResource(R.drawable.buyer_button_shape);
                busan.setBackgroundResource(R.drawable.buyer_button_shape3);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String fullAddress = util.getStringData("주소", ""); // 대구광역시 중구\n가나다라
                if(fullAddress.length() > 4) {
                    String[] s1Address = fullAddress.split("\n"); //0: 대구광역시 중구, 1: 가나다라
                    if(s1Address.length == 2) {
                        String[] s2Address = s1Address[0].split(" "); // 0:대구광역시, 1:중구
                        if(s2Address.length == 2) {
                            if(loadPreOrderAddress(s2Address[0], s2Address[1])) {
                                editText.setText(s1Address[1]);
                            }
                        }
                    }
                }
            }
        }, 800);



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        gps_btn=(ViewGroup)findViewById(R.id.Buyer_gps_btn_layout);
        gps_address=(TextView)findViewById(R.id.Buyer_gps_address);
        gps_img=(ImageView)findViewById(R.id.Buyer_gps_img);
        gps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GpsTracker(Buyer_AddressRegistActivity.this);

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);
                gps_address.setText(address);
                gps_address.setTextColor(Color.parseColor("#000000"));
                gps_img.setImageDrawable(null);

                if (!checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting();
                }else {
                    if(checkRunTimePermission()) {
                        String[] tmp = address.split(" ");
                        //tmp[0] : 대한민국, tmp[1] : 대구광역시
                        //tmp[2] : XX구, tmp[3~] : 상세주소
                        if(loadPreOrderAddress(tmp[1], tmp[2])) {
                            String tt = "";
                            for(int i = 3; i < tmp.length; i++) {
                                tt += tmp[i] + " ";
                            }
                            editText.setText(tt);
                        }
                    }
                }
            }
        });
        findViewById(R.id.Buyer_AddressRegistActivity_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "상세 주소를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(address1.equals("") || address2.equals("")) {
                    Toast.makeText(getApplicationContext(), "주소를 클릭하여 설정하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra("주소1", address1);
                    intent.putExtra("주소2", address2);
                    intent.putExtra("주소3", editText.getText().toString());
                    util.setStringData("주소", address1 + " " + address2 + "\n" + editText.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    public void setAddress2(String province, String address) {
        if(province.equals("대구")) {
            this.address1 = "대구광역시";
            fragment2.selectPrivinceBusan(-1);
        }
        else if(province.equals("부산")) {
            fragment1.selectPrivinceDaegu(-1);
            this.address1 = "부산광역시";
        }
        this.address2 = address;
    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(Buyer_AddressRegistActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(Buyer_AddressRegistActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    Boolean checkRunTimePermission(){
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(Buyer_AddressRegistActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(Buyer_AddressRegistActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
            return true;
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(Buyer_AddressRegistActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(Buyer_AddressRegistActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Buyer_AddressRegistActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Buyer_AddressRegistActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
            return false;
        }
    }
    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Buyer_AddressRegistActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private Boolean loadPreOrderAddress(String t1, String t2) {
        if(t1.equals("대구광역시")) {
            fragmentManager.beginTransaction().hide(fragment2).commit();
            fragmentManager.beginTransaction().show(fragment1).commit();
            daegu.setBackgroundResource(R.drawable.buyer_button_shape3);
            busan.setBackgroundResource(R.drawable.buyer_button_shape);
            address1 = "대구광역시";

            switch(t2) {
                case "남구":
                    fragment1.selectPrivinceDaegu(0); address2 = "남구"; return true;
                case "달서구":
                    fragment1.selectPrivinceDaegu(1); address2 = "달서구"; return true;
                case "동구":
                    fragment1.selectPrivinceDaegu(2); address2 = "동구"; return true;
                case "북구":
                    fragment1.selectPrivinceDaegu(3); address2 = "북구"; return true;
                case "서구":
                    fragment1.selectPrivinceDaegu(4); address2 = "서구"; return true;
                case "수성구":
                    fragment1.selectPrivinceDaegu(5); address2 = "수성구"; return true;
                case "중구":
                    fragment1.selectPrivinceDaegu(6); address2 = "중구"; return true;
                default:
                    address2 = ""; break;
            }
        }
        else if(t1.equals("부산광역시")) {
            fragmentManager.beginTransaction().hide(fragment1).commit();
            fragmentManager.beginTransaction().show(fragment2).commit();
            daegu.setBackgroundResource(R.drawable.buyer_button_shape);
            busan.setBackgroundResource(R.drawable.buyer_button_shape3);
            address1 = "부산광역시";
            switch(t2) {
                case "금정구":
                    fragment2.selectPrivinceBusan(0); address2 = "금정구"; return true;
                case "북구":
                    fragment2.selectPrivinceBusan(1); address2 = "북구"; return true;
                case "동래구":
                    fragment2.selectPrivinceBusan(2); address2 = "동래구"; return true;
                case "연제구":
                    fragment2.selectPrivinceBusan(3); address2 = "연제구"; return true;
                case "남구":
                    fragment2.selectPrivinceBusan(4); address2 = "남구"; return true;
                case "부산진구":
                    fragment2.selectPrivinceBusan(5); address2 = "부산진구"; return true;
                case "동구":
                    fragment2.selectPrivinceBusan(6); address2 = "동구"; return true;
                case "중구":
                    fragment2.selectPrivinceBusan(7); address2 = "중구"; return true;
                case "사상구":
                    fragment2.selectPrivinceBusan(8); address2 = "사상구"; return true;
                case "서구":
                    fragment2.selectPrivinceBusan(9); address2 = "서구"; return true;
                case "사하구":
                    fragment2.selectPrivinceBusan(10); address2 = "사하구"; return true;
                case "해운대구":
                    fragment2.selectPrivinceBusan(11); address2 = "해운대구"; return true;
                default:
                    address2 = ""; break;
            }
        }
        else
            Toast.makeText(Buyer_AddressRegistActivity.this, "해당 주소지는 서비스가 불가능합니다.", Toast.LENGTH_SHORT).show();
        return false;
    }
}

