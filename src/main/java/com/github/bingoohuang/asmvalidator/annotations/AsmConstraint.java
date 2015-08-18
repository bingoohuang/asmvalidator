package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidateGenerator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmConstraint {
    Class<? extends AsmValidateGenerator> validateBy()
            default AsmNoopValidateGenerator.class;

    String message();
}
