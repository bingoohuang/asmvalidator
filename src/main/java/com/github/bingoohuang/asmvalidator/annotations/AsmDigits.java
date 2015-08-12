package com.github.bingoohuang.asmvalidator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmRegex("^\\d+$")
@AsmConstraint(message = "必须为数字")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmDigits {
}
