package com.github.bingoohuang.asmvalidator.annotations;


import com.github.bingoohuang.asmvalidator.validation.AsmDateTimeValidateGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmConstraint(
        supportedClasses = {String.class},
        asmValidateBy = AsmDateTimeValidateGenerator.class,
        message = "时间格式非法")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmDateTime {
    String format() default "yyyy-MM-dd HH:mm:ss";
}
