package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import lombok.Data;

@Data
public class AsmRangeBean2 {
    @AsmRange("[A0,") String upperBound;
}
