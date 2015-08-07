package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmConstraint {
    Class<? extends AsmValidationGenerator> validateBy();
    String message() default "";
}
