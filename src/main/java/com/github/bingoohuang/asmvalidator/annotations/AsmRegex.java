package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmRegexValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmRegexValidationGenerator.class,
        message = "格式非法")
@Target({ElementType.FIELD, ElementType.METHOD,
        ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmRegex {
    String value();
}
