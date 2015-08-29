package com.github.bingoohuang.asmvalidator.annotations;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.MsaValidator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmConstraint {
    Class<?>[] supportedClasses() default {};

    Class<? extends AsmValidateGenerator>[] asmValidateBy() default {};

    String message() default "格式错误";

    boolean allowMessageOverride() default true;

    /**
     * 自定义的非asm形式的校验子.
     * asmvalidator会根据实现的类型按顺序匹配.
     * 如果匹配不到，则认为当前类型不支持.
     *
     * @return 校验子列表.
     */
    Class<? extends MsaValidator>[] validateBy() default {};
}
