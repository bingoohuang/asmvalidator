package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmRegexValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmRegexValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmRegex {
    String value();
}
