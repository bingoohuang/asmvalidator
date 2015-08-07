package com.github.bingoohuang.asmvalidator.annotations;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface AsmIgnore {
}
