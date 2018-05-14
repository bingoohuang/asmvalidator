package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmBlankable;
import com.github.bingoohuang.asmvalidator.annotations.AsmMaxSize;
import com.github.bingoohuang.asmvalidator.annotations.AsmMessage;
import com.github.bingoohuang.asmvalidator.annotations.AsmSize;
import lombok.Data;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2017/2/10.
 */
public class BlackableSizeBeanTest {
    @Test
    public void test() {
        BlackableSizeBean bean = new BlackableSizeBean();
        bean.setPortrait("1223");
        AsmValidateResult result = new AsmValidateResult();
        AsmValidatorFactory.validate(bean, result);
        assertThat(result.hasErrors()).isFalse();
    }

    /**
     * @author bingoohuang [bingoohuang@gmail.com] Created on 2017/2/10.
     */
    @Data
    public static class BlackableSizeBean {
        @AsmMaxSize(7864320) @AsmMessage("图片不能超过5M")
        private String portrait;

        @AsmBlankable @AsmSize(6)
        private String province;
    }
}
