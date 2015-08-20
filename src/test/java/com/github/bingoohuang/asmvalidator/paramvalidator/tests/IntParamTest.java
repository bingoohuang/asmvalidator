package com.github.bingoohuang.asmvalidator.paramvalidator.tests;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmMaxSize;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.validate;
import static org.junit.Assert.assertTrue;

public class IntParamTest {
    private static String validatorSignature;

    public interface IntParams {
        @AsmValid
        String something(@AsmRange("[100,200]") int number, @AsmMaxSize(16) long money);
    }

    @BeforeClass
    public static void beforeClass() {
        Method method = IntParams.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        validatorSignature = createValidatorSignature(method);
    }

    @Test
    public void validMobile() {
        validate(validatorSignature, 123, 234L);
    }

    @Test
    public void tooLargeMobile() {
        try {
            validate(validatorSignature, 201, 234L);
        } catch (AsmValidateException e) {
            assertTrue(e.getMessage().contains("取值不在范围[100,200]"));
        }
    }

    @Test(expected = AsmValidateException.class)
    public void validTooMuchMoney() { // 长度超过64
        validate(validatorSignature, 123, 12345678901234567L);
    }
}
