package com.github.bingoohuang.asmvalidator.annotations;


import com.github.bingoohuang.asmvalidator.validation.AsmMobileValidationGenerator;

import java.lang.annotation.*;

@Documented
@AsmConstraint(validateBy = AsmMobileValidationGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmMobile {
}
