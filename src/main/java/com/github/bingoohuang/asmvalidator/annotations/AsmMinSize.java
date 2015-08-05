package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmMinSizeValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmMinSizeValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmMinSize {
    int value();
}
