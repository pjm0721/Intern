package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

//판매자 메뉴 관리 리스트 데이터 타입
public class SellerRMListData {
    String name;
    String num;
    String price;
    Boolean check;

    public SellerRMListData(String name,String num,String price)
    {
        this.name=name;
        this.num=num;
        this.price=price;
        this.check=false;
    }


}
