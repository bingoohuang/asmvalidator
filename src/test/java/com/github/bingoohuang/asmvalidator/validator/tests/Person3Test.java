package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.*;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import lombok.Data;
import org.junit.Test;

public class Person3Test {
    @Test
    public void asmMinSizeForIntOk() {
        Person3 person3 = new Person3();
        person3.setAge(1234);

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void asmMinSizeForIntErr() {
        Person3 person3 = new Person3();
        person3.setAge(12);

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void asmMinSizeForIntErr2() {
        Person3 person3 = new Person3();
        person3.setAge(12345);

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }

    @Test
    public void asmRegex() {
        Person3 person3 = new Person3();
        person3.setAge(1234);
        person3.setAddr("nanjing");

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void asmRegexError() {
        Person3 person3 = new Person3();
        person3.setAge(1234);
        person3.setAddr("中国");

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }

    @Data
    public static class Person3 {
        @AsmMinSize(3) @AsmMaxSize(10) @AsmSize(4) int age;
        @AsmBlankable @AsmRegex("^\\w+$") String addr;
    }
}
