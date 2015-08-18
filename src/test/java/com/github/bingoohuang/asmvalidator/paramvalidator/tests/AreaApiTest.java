package com.github.bingoohuang.asmvalidator.paramvalidator.tests;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmMessage;
import com.github.bingoohuang.asmvalidator.annotations.AsmRegex;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.validate;

public class AreaApiTest {
    private static String validatorSignature;

    public interface AreaApi {
        @AsmValid
        String getAreaByDistrict(
                @RequestParam("districtCode")
                @AsmRegex("^(?:[1-9][0-9]{5})$") @AsmMessage("区县编码必须是6位数字")
                String districtCode);
    }

    @BeforeClass
    public static void beforeClass() {
        Method method = AreaApi.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        validatorSignature = createValidatorSignature(method);
    }

    @Test
    public void validDistrictCode() {
        validate(validatorSignature, "110123");
    }

    @Test(expected = AsmValidateException.class)
    public void validZoroHeadDistrictCode() {
        validate(validatorSignature, "010123");
    }

    @Test(expected = AsmValidateException.class)
    public void validBadSizeDistrictCode() {
        validate(validatorSignature, "110");
    }
}
