package com.github.bingoohuang.asmvalidator;


import com.github.bingoohuang.asmvalidator.domain.AsmRangeBean2;
import com.github.bingoohuang.asmvalidator.ex.AsmValidatorBadArgException;
import org.junit.Test;

public class AsmRangeTest2 {

    @Test(expected = AsmValidatorBadArgException.class)
    public void testBadArgument(){
        AsmRangeBean2 bean = new AsmRangeBean2();
        bean.setUpperBound("xxx");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }
}
