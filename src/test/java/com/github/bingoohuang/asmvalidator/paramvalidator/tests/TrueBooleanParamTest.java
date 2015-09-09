package com.github.bingoohuang.asmvalidator.paramvalidator.tests;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmTrue;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.validate;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TrueBooleanParamTest {
    private static String validatorSignature;

    public interface TrueBooleanParams {
        @AsmValid
        void something(@AsmTrue Boolean agree);
    }

    @BeforeClass
    public static void beforeClass() {
        Method method = TrueBooleanParams.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        validatorSignature = createValidatorSignature(method);
    }

    @Test
    public void validYes() {
        validate(validatorSignature, true);
    }

    @Test
    public void validNo() {
        try {
            validate(validatorSignature, false);
            fail();
        } catch (AsmValidateException e) {
            assertTrue(e.getMessage().contains("必须为真"));
        }
    }

    @Test
    public void validNull() {
        try {
            validate(validatorSignature, (Boolean) null);
            fail();
        } catch (AsmValidateException e) {
            assertTrue(e.getMessage().contains("取值不能为空"));
        }
    }
}
