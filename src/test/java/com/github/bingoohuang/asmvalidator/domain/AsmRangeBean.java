package com.github.bingoohuang.asmvalidator.domain;

import com.github.bingoohuang.asmvalidator.annotations.*;

public class AsmRangeBean {
    @AsmRange("[10,20]")
    int age;
    @AsmRange("[A00,B99)")
    String addr;

    @AsmRange("男,女")
    String sex;

    @AsmRange("1,5,10,20,50,100")
    int rmb;

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
