package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.Person;
import org.junit.Test;

public class SimpleTest {
    @Test
    public void pass() {
        Person person = new Person();
        person.setName("1234567890");
        person.setAddr("aaa");
        AsmValidator asmValidator = AsmValidatorFactory.getValidator(person.getClass());
        AsmValidateResult result = asmValidator.validate(person);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
    public void startup() {
        Person person = new Person();
        AsmValidator asmValidator = AsmValidatorFactory.getValidator(person.getClass());
        AsmValidateResult result = asmValidator.validate(person);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
    public void tooLong() {
        Person person = new Person();
        person.setName("12345678901");
        person.setAddr("aaa");
        AsmValidator asmValidator = AsmValidatorFactory.getValidator(person.getClass());
        AsmValidateResult result = asmValidator.validate(person);
        result.throwExceptionIfError();
    }
}
