package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmFutureValidateGenerator;

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
        asmValidateBy = AsmFutureValidateGenerator.class,
        message = "需要一个将来的时间")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmFuture {
    String format() default "yyyy-MM-dd HH:mm:ss";
}
