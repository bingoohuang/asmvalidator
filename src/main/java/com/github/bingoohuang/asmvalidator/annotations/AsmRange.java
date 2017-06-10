package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmRangeValidateGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmConstraint(
        supportedClasses = {String.class,
                int.class, Integer.class,
                float.class, Float.class,
                long.class, Long.class,
                Collection.class},
        asmValidateBy = AsmRangeValidateGenerator.class,
        message = "取值不在范围{value}")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmRange {
    String value();
}
