package com.github.bingoohuang.asmvalidator.domain;

import com.github.bingoohuang.asmvalidator.annotations.*;

public class AsmRangeBean {
    @AsmRange("[10,20]")
    int age;
    @AsmRange("[A00,B99)")
    String addr;

    @AsmRange("男,女,人妖")
    String sex;

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
}
