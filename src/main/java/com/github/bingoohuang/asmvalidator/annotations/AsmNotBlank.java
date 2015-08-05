package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmNotEmptyValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmNotEmptyValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmNotBlank {
}
