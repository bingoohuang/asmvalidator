package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmBase64;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import lombok.Data;
import org.junit.Test;

import static com.github.bingoohuang.asmvalidator.annotations.AsmBase64.Base64Format.UrlSafe;

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

    @Data
    public static class Base64Bean {
        @AsmBase64 String base64;
        @AsmBase64(purified = true) String other;
        @AsmBase64(format = UrlSafe) String third;
    }
}
