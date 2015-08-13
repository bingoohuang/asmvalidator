package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmBase64;

import static com.github.bingoohuang.asmvalidator.annotations.AsmBase64.Base64Format.UrlSafe;

public class Base64Bean {
    @AsmBase64
    String base64;
    @AsmBase64(purified = true)
    String other;
    @AsmBase64(format = UrlSafe)
    String third;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }
}
