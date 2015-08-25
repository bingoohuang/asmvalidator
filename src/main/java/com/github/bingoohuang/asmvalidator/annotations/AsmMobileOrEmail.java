package com.github.bingoohuang.asmvalidator.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;


@Documented
// @AsmOrBegin @AsmMobile @AsmEmail @AsmOrEnd
@AsmRegex("^1\\d{10}$" + "|" + "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
@AsmConstraint(
        supportedClasses = String.class,
        message = "必须为手机号码或者邮箱")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmMobileOrEmail {

}

