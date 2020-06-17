package com.example.kyngpook.Seller;

public class SellerReviewItem {
    private String sri_content;
    private String sri_nickname;
    private String sri_time;
    private int review_score;

    public SellerReviewItem(String sri_content, String sri_nickname, String sri_time, int review_score) {
        this.sri_content = sri_content;
        this.sri_nickname = sri_nickname;
        this.sri_time = sri_time;
        this.review_score = review_score;
    }

    public String getSri_content() {
        return sri_content;
    }

    public void setSri_content(String sri_content) {
        this.sri_content = sri_content;
    }

    public String getSri_nickname() {
        return sri_nickname;
    }

    public void setSri_nickname(String sri_nickname) {
        this.sri_nickname = sri_nickname;
    }

    public String getSri_time() {
        return sri_time;
    }

    public void setSri_time(String sri_time) {
        this.sri_time = sri_time;
    }

    public int getReview_score() {
        return review_score;
    }

    public void setReview_score(int review_score) {
        this.review_score = review_score;
    }
}
