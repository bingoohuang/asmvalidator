package com.github.bingoohuang.asmvalidator.validator.tests;


import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateBadArgException;
import com.github.bingoohuang.asmvalidator.validator.domain.AsmRangeBean2;
import org.junit.Test;

public class AsmRangeTest2 {

    @Test(expected = AsmValidateBadArgException.class)
    public void testBadArgument() {
        AsmRangeBean2 bean = new AsmRangeBean2();
        bean.setUpperBound("xxx");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }
}
