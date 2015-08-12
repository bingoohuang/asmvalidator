package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmMaxSizeValidationGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmConstraint(validateBy = AsmMaxSizeValidationGenerator.class,
        message = "长度超过{value}")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmMaxSize {
    int value();
}
