package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import lombok.Data;

@Data
public class AsmRangeBean {
    @AsmRange("[10,100]") int age;
    @AsmRange("[A00,B99)") String addr;
    @AsmRange("男,女") String sex;
    @AsmRange("1,5,10,20,50,100") int rmb;
    @AsmRange("[10,]") int ageMin;
    @AsmRange("[,10]") int ageMax;
    @AsmRange("[A00,]") String code;
}
