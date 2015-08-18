package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.github.bingoohuang.asmvalidator.validator.domain.Base64Bean;
import org.junit.Test;

public class Base64BeanTest {
    @Test
    public void okBase64() {
        Base64Bean base64Bean = new Base64Bean();
        base64Bean.setBase64("YWJjZGU=");
        base64Bean.setOther("YWJjZGU=");
        base64Bean.setThird("5LuAS-S4nEzllYo_");
        AsmValidatorFactory.validateWithThrow(base64Bean);
    }

    @Test(expected = AsmValidateException.class)
    public void badUrlSafe() {
        Base64Bean base64Bean = new Base64Bean();
        base64Bean.setBase64("YWJjZGU=");
        base64Bean.setOther("YWJjZGU=");
        base64Bean.setThird("5LuAS+S4nEzllYo/");
        AsmValidatorFactory.validateWithThrow(base64Bean);
    }

    @Test
    public void purifiedBase64Ok() {
        Base64Bean base64Bean = new Base64Bean();
        base64Bean.setBase64("YWJjZGU=");
        base64Bean.setOther("YWJjZGU");
        base64Bean.setThird("5LuAS-S4nEzllYo_");
        AsmValidatorFactory.validateWithThrow(base64Bean);
    }

    @Test(expected = AsmValidateException.class)
    public void purifiedBase64() {
        Base64Bean base64Bean = new Base64Bean();
        base64Bean.setBase64("YWJjZGU");
        base64Bean.setOther("YWJjZGU");
        base64Bean.setThird("5LuAS-S4nEzllYo_");
        AsmValidatorFactory.validateWithThrow(base64Bean);
    }


    @Test(expected = AsmValidateException.class)
    public void badBase64() {
        Base64Bean base64Bean = new Base64Bean();
        base64Bean.setBase64("YWJjZG*aa");
        base64Bean.setOther("YWJjZGU");
        base64Bean.setThird("5LuAS-S4nEzllYo_");
        AsmValidatorFactory.validateWithThrow(base64Bean);
    }
}
