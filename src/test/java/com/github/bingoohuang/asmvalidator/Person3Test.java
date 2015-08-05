package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.Person3;
import org.junit.Test;

public class Person3Test {
    @Test
    public void asmMinSizeForIntOk() {
        Person3 person3 = new Person3();
        person3.setAge(1234);

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
    public void asmMinSizeForIntErr() {
        Person3 person3 = new Person3();
        person3.setAge(12);

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
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

    @Test(expected = AsmValidatorException.class)
    public void asmRegexError() {
        Person3 person3 = new Person3();
        person3.setAge(1234);
        person3.setAddr("中国");

        AsmValidateResult result = AsmValidatorFactory.validate(person3);
        result.throwExceptionIfError();
    }
}
