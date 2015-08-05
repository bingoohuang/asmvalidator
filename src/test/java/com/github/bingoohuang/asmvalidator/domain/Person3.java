package com.github.bingoohuang.asmvalidator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmBlankable;
import com.github.bingoohuang.asmvalidator.annotations.AsmMinSize;

public class Person3 {
    @AsmMinSize(3)
    int age;
    @AsmBlankable
    String addr;

    public Person3() {
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
}
