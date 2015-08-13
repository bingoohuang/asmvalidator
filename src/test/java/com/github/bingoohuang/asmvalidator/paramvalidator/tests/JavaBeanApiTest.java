package com.github.bingoohuang.asmvalidator.paramvalidator.tests;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmMobile;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.ex.AsmValidatorException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.validate;

public class JavaBeanApiTest {
    private static String validatorSignature;

    public interface BeanApi {
        @AsmValid
        String getAreaByDistrict(
                @RequestParam("districtCode")
                DistrictCode districtCode);
    }

    @BeforeClass
    public static void beforeClass() {
        Method method = BeanApi.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        validatorSignature = createValidatorSignature(method);
    }

    @Test
    public void validDistrictCode() {
        DistrictCode districtCode = new DistrictCode();
        districtCode.setMobile("18602506990");
        validate(validatorSignature, districtCode);
    }

    @Test(expected = AsmValidatorException.class)
    public void badDistrictCode() {
        DistrictCode districtCode = new DistrictCode();
        districtCode.setMobile("X8602506990");
        validate(validatorSignature, districtCode);
    }


    @AsmValid
    public static class DistrictCode {
        @AsmMobile
        private String mobile;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
