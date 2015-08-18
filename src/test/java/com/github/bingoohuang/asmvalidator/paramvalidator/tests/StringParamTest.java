package com.github.bingoohuang.asmvalidator.paramvalidator.tests;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmMobile;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.validate;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StringParamTest {
    private static String validatorSignature;

    public interface StringParams {
        @AsmValid
        String something(@AsmMobile String mobile);
    }

    @BeforeClass
    public static void beforeClass() {
        Method method = StringParams.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        validatorSignature = createValidatorSignature(method);
    }

    @Test
    public void validMobile() {
        validate(validatorSignature, "18602506990");
    }

    @Test
    public void nullMobile() {
        try {
            validate(validatorSignature, (String) null);
            fail();
        } catch (AsmValidateException e) {
            assertTrue(e.getMessage().contains("字段不能为空"));
        }
    }

    @Test
    public void badMobile() {
        try {
            validate(validatorSignature, "28602506990");
            fail();
        } catch (AsmValidateException e) {
            assertTrue(e.getMessage().contains("手机号码格式非法"));
        }
    }
}
