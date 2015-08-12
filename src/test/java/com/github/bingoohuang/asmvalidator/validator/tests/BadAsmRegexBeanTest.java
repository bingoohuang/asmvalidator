package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmRegex;
import com.github.bingoohuang.asmvalidator.ex.AsmValidatorBadArgException;
import org.junit.Test;

public class BadAsmRegexBeanTest {
    @Test(expected = AsmValidatorBadArgException.class)
    public void test() {
        BadAsmRegexBean badAsmRegexBean = new BadAsmRegexBean();
        AsmValidatorFactory.validate(badAsmRegexBean);
    }

    public static class BadAsmRegexBean {
        @AsmRegex("[poiu")
        public String regex;


        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }
    }
}
