package com.github.bingoohuang.asmvalidator.annotations;


import com.github.bingoohuang.asmvalidator.validation.AsmIngoreValidator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.LOCAL_VARIABLE,ElementType.METHOD})
@AsmConstraint(validateBy = AsmIngoreValidator.class)
public @interface AsmIngore {
}
