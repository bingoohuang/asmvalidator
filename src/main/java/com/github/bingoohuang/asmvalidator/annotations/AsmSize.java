package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmSizeValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmSizeValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmSize {
    int value();
}
