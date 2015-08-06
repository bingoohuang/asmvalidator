package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.annotations.AsmRegex;
import com.github.bingoohuang.asmvalidator.ex.AsmValidatorAnnotationBadArgumentException;
import org.junit.Test;

public class BadAsmRegexBeanTest {
    @Test(expected = AsmValidatorAnnotationBadArgumentException.class)
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
