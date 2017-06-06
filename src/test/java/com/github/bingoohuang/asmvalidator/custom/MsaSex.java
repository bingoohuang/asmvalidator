package com.github.bingoohuang.asmvalidator.custom;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.MsaValidator;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@AsmConstraint(validateBy = MsaSex.MsaSexValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MsaSex {
    boolean allowLadyboy() default false;

    class MsaSexValidator implements MsaValidator<MsaSex, String> {
        @Override
        public void validate(MsaSex msaSex, AsmValidateResult result, String sex) {
            if ("男".equals(sex) || "女".equals(sex)) return;
            if (msaSex.allowLadyboy() && "人妖".equals(sex)) return;

            result.addError(new ValidateError("sex", sex, "性别非法"));
        }

    }
}
