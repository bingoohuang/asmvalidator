package com.github.bingoohuang.asmvalidator.custom;

import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@AsmConstraint(validateBy = MsaSexValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MsaSex {
    boolean allowLadyboy() default false;
}
