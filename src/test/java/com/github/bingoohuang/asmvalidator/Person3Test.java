package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.Person3;
import org.junit.Ignore;
import org.junit.Test;

public class Person3Test {
    @Test
    @Ignore
    public void thatAsmMinSizeOnlyForCharSequence() {
        Person3 person3 = new Person3();

        AsmValidatorFactory.validate(person3);
    }
}
