package com.github.bingoohuang.asmvalidator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmMobile;

public class MobileBean {

    @AsmMobile
    String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
