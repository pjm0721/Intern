package com.example.kyngpook.Buyer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kyngpook.R;

import androidx.fragment.app.Fragment;

public class Buyer_Address_Busan_Fragment extends Fragment {
    Buyer_AddressRegistActivity activity;
    ViewGroup rootView;

    Button[] arrBtn = new Button[12];

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
        rootView = (ViewGroup) inflater.inflate(R.layout.buyer_address_busan, container, false);


        arrBtn[0] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn1);
        arrBtn[1] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn2);
        arrBtn[2] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn3);
        arrBtn[3] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn4);
        arrBtn[4] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn5);
        arrBtn[5] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn6);
        arrBtn[6] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn7);
        arrBtn[7] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn8);
        arrBtn[8] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn9);
        arrBtn[9] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn10);
        arrBtn[10] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn11);
        arrBtn[11] = rootView.findViewById(R.id.Buyer_AddressFragmentBusan_mBtn12);

        for(int i = 0; i < 12; i++) {
            final int finalI = i;
            arrBtn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address2 = selectPrivinceBusan(finalI);
                    activity.setAddress2("부산", address2);
                }
            });
        }

        return rootView;
    }
    public String selectPrivinceBusan(int i) {
        for(int j = 0; j < 12; j++) {
            arrBtn[j].setBackgroundResource(R.drawable.buyer_button_shape);
        }
        if(i == -1) return "null";
        arrBtn[i].setBackgroundResource(R.drawable.buyer_button_shape2);
        switch (i) {
            case 0:
                return "금정구";
            case 1:
                return "북구";
            case 2:
                return "동래구";
            case 3:
                return "연제구";
            case 4:
                return "남구";
            case 5:
                return "부산진구";
            case 6:
                return "동구";
            case 7:
                return "중구";
            case 8:
                return "사상구";
            case 9:
                return "서구";
            case 10:
                return "사하구";
            case 11:
                return "해운대구";
            default:
                return "null";
        }
    }
}