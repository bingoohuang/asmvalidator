package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.junit.Test;

import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class RangeListTest {
    @Data @Builder
    public static class RangeListBean {
        @AsmRange("[1,7]")
        private Collection<Integer> days;
    }

    @Test
    public void testOK() {
        val bean = RangeListBean.builder().days(Lists.newArrayList(1, 2, 3)).build();
        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void testBad() {
        val bean = RangeListBean.builder().days(Lists.newArrayList(0)).build();
        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (AsmValidateException ex) {
            assertThat(ex.getMessage()).contains("取值不在范围[1,7]");
        }
    }
}
