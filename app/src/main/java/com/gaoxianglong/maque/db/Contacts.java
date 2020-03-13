package com.gaoxianglong.maque.db;

import org.litepal.crud.LitePalSupport;

public class Contacts extends LitePalSupport {
    private int id;
    private String sex;
    private String number;
    private String remarks;
    private String qq;
    private String birthday;
    private String describe;

    public Contacts() {
    }

    public Contacts(String sex, String number, String remarks, String qq, String birthday, String describe) {
        this.sex = sex;
        this.number = number;
        this.remarks = remarks;
        this.qq = qq;
        this.birthday = birthday;
        this.describe = describe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
