package com.github.bingoohuang.asmvalidator.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmRegex("^1\\d{10}$")
@AsmConstraint(
        supportedClasses = String.class,
        message = "手机号码格式非法")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmMobile {
}
