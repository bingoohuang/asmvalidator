package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.asm.AsmValidatorClassGenerator;
import com.github.bingoohuang.utils.lang.Fucks;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.objenesis.ObjenesisStd;

import java.util.Collection;
import java.util.concurrent.Callable;

@UtilityClass
public class AsmValidatorFactory {
    private Cache<Class, AsmValidator<Object>>
            cache = CacheBuilder.newBuilder().build();

    public AsmValidator<Object> getValidator(final Class<?> beanClass) {
        try {
            return cache.get(beanClass, new Callable<AsmValidator<Object>>() {
                public AsmValidator<Object> call() {
                    return asmCreate(beanClass);
                }
            });
        } catch (Exception e) {
            Throwable ex = e.getCause() != null ? e.getCause() : e;
            throw Fucks.fuck(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private AsmValidator<Object> asmCreate(Class<?> beanClass) {
        val generator = new AsmValidatorClassGenerator(beanClass);
        val asmValidatorClass = generator.generate();

        val objenesisStd = new ObjenesisStd();
        val asmValidator = objenesisStd.newInstance(asmValidatorClass);

        return (AsmValidator<Object>) asmValidator;
    }

    public AsmValidateResult validate(Object bean) {
        val validator = getValidator(bean.getClass());
        return validator.validate(bean);
    }


    public void validateAll(Collection<?> beans, AsmValidateResult asmValidateResult) {
        if (beans == null) return;

        for (Object bean : beans) {
            val validator = getValidator(bean.getClass());
            asmValidateResult.addErrors(validator.validate(bean));
        }
    }

    public void validate(Object bean, AsmValidateResult asmValidateResult) {
        if (bean == null) return;

        val validator = getValidator(bean.getClass());
        asmValidateResult.addErrors(validator.validate(bean));
    }

    public void validateWithThrow(Object bean) {
        val validator = getValidator(bean.getClass());
        validator.validate(bean).throwExceptionIfError();
    }
}
