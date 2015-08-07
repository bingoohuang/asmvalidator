package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmNoopValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmUUID {
}
