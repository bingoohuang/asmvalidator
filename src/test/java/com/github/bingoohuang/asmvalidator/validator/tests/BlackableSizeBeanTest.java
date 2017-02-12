package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.validator.domain.BlackableSizeBean;
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
}
