package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.validator.domain.Person2;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.Test;

public class Person2Test {
    @Test
    public void ok() {
        Person2 person2 = new Person2();
        person2.setName("bingoo");
        person2.setAddr(null);

        AsmValidateResult result = AsmValidatorFactory.validate(person2);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void nameRequired() {
        Person2 person2 = new Person2();
        person2.setName(null);
        person2.setAddr(null);

        AsmValidateResult result = AsmValidatorFactory.validate(person2);
        result.throwExceptionIfError();
    }

    @Test
    public void minSize() {
        Person2 person2 = new Person2();
        person2.setName("abc");
        person2.setAddr(null);

        AsmValidateResult result = AsmValidatorFactory.validate(person2);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void minSizeBad() {
        Person2 person2 = new Person2();
        person2.setName("ab");
        person2.setAddr(null);

        AsmValidateResult result = AsmValidatorFactory.validate(person2);
        result.throwExceptionIfError();
    }
}
