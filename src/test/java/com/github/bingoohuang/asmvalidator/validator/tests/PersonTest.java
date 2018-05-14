package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

public class PersonTest {
    @Test
    public void pass() {
        Person person = new Person();
        person.setName("1234567890");
        person.setAddr("aaa");
        AsmValidateResult result = AsmValidatorFactory.validate(person);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void startup() {
        Person person = new Person();
        AsmValidateResult result = AsmValidatorFactory.validate(person);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void tooLong() {
        Person person = new Person();
        person.setName("1234567890123456789012345678901234567890123456789012345678901234567");
        person.setAddr("aaa");
        AsmValidateResult result = AsmValidatorFactory.validate(person);
        result.throwExceptionIfError();
    }

    @Data @NoArgsConstructor
    public static class Person {
        String name;
        String addr;

        @AsmIgnore String code;

        public Person(String name, String addr) {
            this.name = name;
            this.addr = addr;
        }
    }
}
