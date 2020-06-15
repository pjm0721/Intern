package com.example.kyngpook.Deliver;

import com.example.kyngpook.R;

import com.google.firebase.firestore.DocumentReference;

import java.util.Map;

public class Deliver_Main_Item {
    private DocumentReference DMI_Document_Reference;
    private String DMI_Buyer_Id;
    private String DMI_Buyer_Address;
    private String DMI_Buyer_Name;
    private int DMI_Price;
    private boolean DMI_Review;
    private String DMI_Seller_Name;
    private String DMI_Seller_Id;
    private String DMI_Seller_Address;
    private String DMI_Order_Time;
    private Map DMI_Order_Info;
    private String DMI_Deliver_Id;
    private String DMI_document;

    public DocumentReference getDMI_Document_Reference() {
        return DMI_Document_Reference;
    }

    public void setDMI_Document_Reference(DocumentReference DMI_Document_Reference) {
        this.DMI_Document_Reference = DMI_Document_Reference;
    }

    public String getDMI_Buyer_Id() {
        return DMI_Buyer_Id;
    }

    public void setDMI_Buyer_Id(String DMI_Buyer_Id) {
        this.DMI_Buyer_Id = DMI_Buyer_Id;
    }

    public String getDMI_Buyer_Address() {
        return DMI_Buyer_Address;
    }

    public void setDMI_Buyer_Address(String DMI_Buyer_Address) {
        this.DMI_Buyer_Address = DMI_Buyer_Address;
    }

    public String getDMI_Buyer_Name() {
        return DMI_Buyer_Name;
    }

    public void setDMI_Buyer_Name(String DMI_Buyer_Name) {
        this.DMI_Buyer_Name = DMI_Buyer_Name;
    }

    public int getDMI_Price() {
        return DMI_Price;
    }

    public void setDMI_Price(int DMI_Price) {
        this.DMI_Price = DMI_Price;
    }

    public boolean isDMI_Review() {
        return DMI_Review;
    }

    public void setDMI_Review(boolean DMI_Review) {
        this.DMI_Review = DMI_Review;
    }

    public String getDMI_Seller_Name() {
        return DMI_Seller_Name;
    }

    public void setDMI_Seller_Name(String DMI_Seller_Name) {
        this.DMI_Seller_Name = DMI_Seller_Name;
    }

    public String getDMI_Seller_Id() {
        return DMI_Seller_Id;
    }

    public void setDMI_Seller_Id(String DMI_Seller_Id) {
        this.DMI_Seller_Id = DMI_Seller_Id;
    }

    public String getDMI_Seller_Address() {
        return DMI_Seller_Address;
    }

    public void setDMI_Seller_Address(String DMI_Seller_Address) {
        this.DMI_Seller_Address = DMI_Seller_Address;
    }

    public String getDMI_Order_Time() {
        return DMI_Order_Time;
    }

    public void setDMI_Order_Time(String DMI_Order_Time) {
        this.DMI_Order_Time = DMI_Order_Time;
    }

    public Map getDMI_Order_Info() {
        return DMI_Order_Info;
    }

    public void setDMI_Order_Info(Map DMI_Order_Info) {
        this.DMI_Order_Info = DMI_Order_Info;
    }

    public String getDMI_Deliver_Id() {
        return DMI_Deliver_Id;
    }

    public void setDMI_Deliver_Id(String DMI_Deliver_Id) {
        this.DMI_Deliver_Id = DMI_Deliver_Id;
    }

    public String getDMI_document() {
        return DMI_document;
    }

    public void setDMI_document(String DMI_document) {
        this.DMI_document = DMI_document;
    }

    public Deliver_Main_Item(DocumentReference DMI_Document_Reference,
                             String DMI_Buyer_Id,
                             String DMI_Buyer_Address,
                             String DMI_Buyer_Name,
                             int DMI_Price,
                             boolean DMI_Review,
                             String DMI_Seller_Name,
                             String DMI_Seller_Id,
                             String DMI_Seller_Address,
                             String DMI_Order_Time,
                             Map DMI_Order_Info,
                             String DMI_Deliver_Id,
                             String DMI_document) {
        this.DMI_Document_Reference = DMI_Document_Reference;
        this.DMI_Buyer_Id = DMI_Buyer_Id;
        this.DMI_Buyer_Address = DMI_Buyer_Address;
        this.DMI_Buyer_Name = DMI_Buyer_Name;
        this.DMI_Price = DMI_Price;
        this.DMI_Review = DMI_Review;
        this.DMI_Seller_Name = DMI_Seller_Name;
        this.DMI_Seller_Id = DMI_Seller_Id;
        this.DMI_Seller_Address = DMI_Seller_Address;
        this.DMI_Order_Time = DMI_Order_Time;
        this.DMI_Order_Info = DMI_Order_Info;
        this.DMI_Deliver_Id = DMI_Deliver_Id;
        this.DMI_document = DMI_document;
    }
}
