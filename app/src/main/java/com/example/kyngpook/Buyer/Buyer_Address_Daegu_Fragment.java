package com.example.kyngpook.Buyer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kyngpook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Buyer_Address_Daegu_Fragment extends Fragment {
    Buyer_AddressRegistActivity activity;
    ViewGroup rootView;

    Button[] arrBtn = new Button[7];

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Buyer_AddressRegistActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.buyer_address_daegu, container, false);

        arrBtn[0] = rootView.findViewById(R.id.Buyer_AddressFragmentDaegu_mBtn1);
        arrBtn[1] = rootView.findViewById(R.id.Buyer_AddressFragmentDaegu_mBtn2);
        arrBtn[2] = rootView.findViewById(R.id.Buyer_AddressFragmentDaegu_mBtn3);
        arrBtn[3] = rootView.findViewById(R.id.Buyer_AddressFragmentDaegu_mBtn4);
        arrBtn[4] = rootView.findViewById(R.id.Buyer_AddressFragmentDaegu_mBtn5);
        arrBtn[5] = rootView.findViewById(R.id.Buyer_AddressFragmentDaegu_mBtn6);
        arrBtn[6] = rootView.findViewById(R.id.Buyer_AddressFragmentDaegu_mBtn7);

        for(int i = 0; i < 7; i++) {
            final int finalI = i;
            arrBtn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address2 = selectPrivinceDaegu(finalI);
                    activity.setAddress2("대구", address2);
                }
            });
        }

        return rootView;
    }

    public String selectPrivinceDaegu(int i) {
        for(int j = 0; j < 7; j++) {
            arrBtn[j].setBackgroundResource(R.drawable.buyer_button_shape);
        }
        if(i == -1) return "null";
        arrBtn[i].setBackgroundResource(R.drawable.buyer_button_shape2);
        switch (i) {
            case 0:
                return "남구";
            case 1:
                return "달서구";
            case 2:
                return "동구";
            case 3:
                return "북구";
            case 4:
                return "서구";
            case 5:
                return "수성구";
            case 6:
                return "중구";
            default:
                return "null";
        }
    }
}