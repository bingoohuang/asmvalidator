package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class FloatRangeTest {
    @Data @AllArgsConstructor
    public static class FloatRangeBean {
        @AsmRange("[0,1000]")
        private float price;        // 价格，单位元
        @AsmRange("[10,1100]")
        private long money;

        @AsmRange("[0,2000]")
        private Float fprice;        // 价格，单位元
        @AsmRange("[10,3100]")
        private Long lmoney;
    }

    @Test
    public void test() {
        FloatRangeBean bean = new FloatRangeBean(2000f, 9000L, 3000f, 4000L);
        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (AsmValidateException ex) {
            assertThat(ex.getMessage()).contains("取值不在范围[0,1000]");
            assertThat(ex.getMessage()).contains("取值不在范围[10,1100]");
            assertThat(ex.getMessage()).contains("取值不在范围[0,2000]");
            assertThat(ex.getMessage()).contains("取值不在范围[10,3100]");
        }
    }
}
