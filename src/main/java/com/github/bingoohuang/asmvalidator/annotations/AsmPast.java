package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmPastValidateGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Calendar;
import java.util.Date;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmConstraint(
        supportedClasses = {String.class, Date.class, Calendar.class},
        asmValidateBy = AsmPastValidateGenerator.class,
        message = "需要一个过去的时间")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmPast {
    String format() default "yyyy-MM-dd HH:mm:ss";
}
