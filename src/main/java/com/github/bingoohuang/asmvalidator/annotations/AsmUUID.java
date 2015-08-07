package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmSize(36) // 0093C1B7-70F2-4ADC-9F06-67DEAF4D0348
@AsmRegex("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}")
@AsmConstraint(validateBy = AsmNoopValidationGenerator.class,
        message = "格式不符合UUID")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmUUID {
}
