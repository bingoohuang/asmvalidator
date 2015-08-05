package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmBlankableValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmBlankableValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmBlankable {
}
