package com.gaoxianglong.maque.db;

import org.litepal.crud.LitePalSupport;

public class TelephoneBook extends LitePalSupport {
    private int id;
    private String pinyin;
    private String number;
    private String name;
    private String imgpath;

    public TelephoneBook(String number, String name, String imgpath) {
        this.number = number;
        this.name = name;
        this.imgpath = imgpath;
    }

    public TelephoneBook() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
