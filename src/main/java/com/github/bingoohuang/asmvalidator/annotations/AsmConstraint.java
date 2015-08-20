package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.MsaValidator;
import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidateGenerator;
import com.github.bingoohuang.asmvalidator.validation.MsaNoopValidator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmConstraint {
    Class<? extends AsmValidateGenerator> asmValidateBy()
            default AsmNoopValidateGenerator.class;

    String message() default "格式错误";
    boolean allowMessageOverride() default true;

    Class<? extends MsaValidator> validateBy() default MsaNoopValidator.class;
}
