package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmDateTime;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DateTimeTest {
    @Data @AllArgsConstructor
    public static class DateTimeBean {
        @AsmDateTime(format = "yyyy-MM") private String date;
    }

    @Test
    public void testOk() {
        val bean = new DateTimeBean("2017-06");
        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void testBad() {
        val bean = new DateTimeBean("17-06");
        try {
            AsmValidatorFactory.validateWithThrow(bean);
            Assert.fail();
        } catch (AsmValidateException ex) {
            assertTrue(ex.getMessage().contains("时间格式非法"));
        }
    }
}
