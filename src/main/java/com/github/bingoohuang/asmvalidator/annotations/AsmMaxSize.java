package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmMaxSizeValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmMaxSizeValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmMaxSize {
    int value();
}
