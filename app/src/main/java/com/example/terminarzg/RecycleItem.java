package com.example.terminarzg;

public class RecycleItem {
    private int imgRes;
    private String text1;
    private String text2;
    private int imgDelRes;
    private int imgAddRes;

    public RecycleItem(int imgRes, String text1, String text2, int imgDelRes, int imgAddRes){
        this.imgRes = imgRes;
        this.text1 = text1;
        this.text2 = text2;
        this.imgDelRes = imgDelRes;
        this.imgAddRes = imgAddRes;
    }


    public int getImgRes(){
        return imgRes;
    }
    public String getText1(){
        return text1;
    }
    public String getText2(){
        return text2;
    }
    public int getImgDelRes(){return imgDelRes; }
    public int getImgAddRes(){return imgAddRes;}


}
