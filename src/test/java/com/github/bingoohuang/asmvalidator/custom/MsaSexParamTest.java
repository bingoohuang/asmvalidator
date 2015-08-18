package com.github.bingoohuang.asmvalidator.custom;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.validate;

public class MsaSexParamTest {
    private static String validatorSignature;

    public interface SexApi {
        @AsmValid
        String querySex(@MsaSex String sex,
                        @MsaSex(allowLadyboy = true) String sex2);
    }

    @BeforeClass
    public static void beforeClass() {
        Method method = SexApi.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        validatorSignature = createValidatorSignature(method);
    }

    @Test
    public void validMale() {
        validate(validatorSignature, "男", "女");
    }

    @Test
    public void validFemale() {
        validate(validatorSignature, "女", "女");
    }

    @Test
    public void validLadyBody() {
        validate(validatorSignature, "女", "人妖");
    }

    @Test(expected = AsmValidateException.class)
    public void validBadLadyBaoy() {
        validate(validatorSignature, "人妖", "人妖");
    }

    @Test(expected = AsmValidateException.class)
    public void bad() {
        validate(validatorSignature, "X", "Y");
    }

}
