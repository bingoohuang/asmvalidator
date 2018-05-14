package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmNotBlank;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class AsmNotBlankTest {
    @Data
    public static class AsmNotBlankBean {
        @AsmNotBlank
        private AtomicBoolean some;
    }

    @Test
    public void testNotBlank() {
        AsmNotBlankBean bean = new AsmNotBlankBean();
        bean.setSome(new AtomicBoolean(true));

        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void testBlank() {
        AsmNotBlankBean bean = new AsmNotBlankBean();

        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("取值不能为空");
        }
    }

}
