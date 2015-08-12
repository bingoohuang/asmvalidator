package com.github.bingoohuang.asmvalidator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmSize(36) // 0093C1B7-70F2-4ADC-9F06-67DEAF4D0348
@AsmRegex("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}")
@AsmConstraint(message = "格式不符合UUID")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmUUID {
}
