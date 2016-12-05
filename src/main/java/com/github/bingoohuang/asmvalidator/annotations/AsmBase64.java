package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.validation.AsmBase64ValidateGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@AsmConstraint(
        supportedClasses = String.class,
        asmValidateBy = AsmBase64ValidateGenerator.class,
        message = "不符合BASE64编码格式")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmBase64 {
    /**
     * 指定是否消除尾部等号。
     * @return 是否消除尾部等号
     */
    boolean purified() default false;

    /**
     * 指定BASE64格式
     * @return BASE64格式
     */
    Base64Format format() default Base64Format.Standard;

    enum Base64Format {
        Standard,
        // URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548)
        UrlSafe
    }
}
