package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmMobile;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;

@AsmValid
public class MobileBean {
    @AsmMobile
    String mobile;

    public MobileBean() {
    }

    public MobileBean(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
