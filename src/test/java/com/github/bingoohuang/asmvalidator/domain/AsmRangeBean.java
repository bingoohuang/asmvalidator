package com.github.bingoohuang.asmvalidator.domain;

import com.github.bingoohuang.asmvalidator.annotations.*;

public class AsmRangeBean {
    @AsmRange("[10,100]")
    int age;
    @AsmRange("[A00,B99)")
    String addr;

    @AsmRange("男,女")
    String sex;

    @AsmRange("1,5,10,20,50,100")
    int rmb;

    @AsmRange("[10,]")
    int ageMin;

    @AsmRange("[,10]")
    int ageMax;


    public int getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(int ageMax) {
        this.ageMax = ageMax;
    }

    public int getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(int ageMin) {
        this.ageMin = ageMin;
    }

    public AsmRangeBean() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getRmb() {
        return rmb;
    }

    public void setRmb(int rmb) {
        this.rmb = rmb;
    }
}
