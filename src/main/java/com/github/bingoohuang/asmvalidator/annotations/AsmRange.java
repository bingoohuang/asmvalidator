package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmRangeValidationGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmConstraint(validateBy = AsmRangeValidationGenerator.class,
        message = "取值不在范围{value}")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmRange {
    String value();
}
