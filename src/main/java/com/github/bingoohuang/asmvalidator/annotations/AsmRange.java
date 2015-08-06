package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmRangeValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmRangeValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmRange {
    String value();
}
