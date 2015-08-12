package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmRegexValidationGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmConstraint(validateBy = AsmRegexValidationGenerator.class,
        message = "格式非法")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmRegex {
    String value();
}
