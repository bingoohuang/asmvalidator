package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.asm.AsmValidatorClassGenerator;
import com.github.bingoohuang.utils.lang.Fucks;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.objenesis.ObjenesisStd;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class AsmValidatorFactory {
    private static Cache<Class, AsmValidator> cache = CacheBuilder.newBuilder().build();

    public static AsmValidator getValidator(final Class<?> beanClass) {
//        return new AsmValidatorPersonImpl();
        try {
            return cache.get(beanClass, new Callable<AsmValidator>() {
                public AsmValidator call() throws Exception {
                    return asmCreate(beanClass);
                }
            });
        } catch (ExecutionException e) {
            Throwable ex = e.getCause() != null ? e.getCause() : e;
            throw Fucks.fuck(ex);
        }
    }

    private static AsmValidator asmCreate(Class<?> beanClass) {
        AsmValidatorClassGenerator generator = new AsmValidatorClassGenerator(beanClass);
        Class<?> asmValidatorClass = generator.generate();

        ObjenesisStd objenesisStd = new ObjenesisStd();
        Object asmValidator = objenesisStd.newInstance(asmValidatorClass);

        return (AsmValidator) asmValidator;
    }

    public static AsmValidateResult validate(Object bean) {
        return getValidator(bean.getClass()).validate(bean);
    }
}
