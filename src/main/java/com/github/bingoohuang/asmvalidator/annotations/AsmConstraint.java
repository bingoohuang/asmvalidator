package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidationGenerator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmConstraint {
    Class<? extends AsmValidationGenerator> validateBy()
            default AsmNoopValidationGenerator.class;

    String message();
}
