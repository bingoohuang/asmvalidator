package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmRange;

public class AsmRangeBean2 {
    @AsmRange("[A0,")
    String upperBound;

    public String getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }
}
