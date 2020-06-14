package com.example.kyngpook.Seller;

import com.example.kyngpook.R;

//판매자 주문내역 리스트를 위한 데이터 타입
public class SellerOrderListData {
    String price;
    String state;
    String address;

    public SellerOrderListData(String price,String state,String address){
        this.address=address;
        this.state=state;
        this.price=price;
    }
}
