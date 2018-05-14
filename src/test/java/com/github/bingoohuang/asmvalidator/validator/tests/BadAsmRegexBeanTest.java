package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmRegex;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateBadArgException;
import lombok.Data;
import org.junit.Test;

public class BadAsmRegexBeanTest {
    @Test(expected = AsmValidateBadArgException.class)
    public void test() {
        BadAsmRegexBean badAsmRegexBean = new BadAsmRegexBean();
        AsmValidatorFactory.validate(badAsmRegexBean);
    }

    @Data
    public static class BadAsmRegexBean {
        @AsmRegex("[poiu")
        public String regex;
    }
}
