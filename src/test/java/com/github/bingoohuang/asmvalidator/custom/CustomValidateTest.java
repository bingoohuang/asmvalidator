package com.github.bingoohuang.asmvalidator.custom;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import lombok.Data;
import lombok.val;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class CustomValidateTest {
    @Data
    public static class CustomValidateBean {
        private String name1;
        private String name2;

        @AsmValid
        public void validate() {
            name1 = name1 + " was validated";
        }

        @AsmValid
        public void validate2(AsmValidateResult result) {
            name2 = name2 + " was validated by validate2";
            result.addError(new ValidateError("xx", "xx", "xx"));
        }
    }

    @Test
    public void test() {
        val b = new CustomValidateBean();
        b.setName1("bingoo");
        b.setName2("huang");

        AsmValidateResult result = AsmValidatorFactory.validate(b);
        assertThat(b.getName1()).isEqualTo("bingoo was validated");
        assertThat(b.getName2()).isEqualTo("huang was validated by validate2");
        assertThat(result.getErrors().size()).isEqualTo(1);
    }
}
