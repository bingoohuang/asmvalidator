package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.asm.AsmParamValidatorClassGenerator;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.primitives.UnsignedInts;
import org.apache.commons.lang3.StringUtils;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Method;

public class AsmParamsValidatorFactory {
    private static Cache<String, AsmValidator>
            cache = CacheBuilder.newBuilder().build();


    public static AsmValidator getValidator(
            String methodSignature, int parameterIndex) {
        return cache.getIfPresent(createKey(methodSignature, parameterIndex));
    }

    private static String createKey(String methodSignature, int parameterIndex) {
        return methodSignature + "." + parameterIndex;
    }

    private static AsmValidator asmCreate(Method method, int parameterIndex) {
        AsmParamValidatorClassGenerator generator
                = new AsmParamValidatorClassGenerator(method, parameterIndex);
        Class<?> asmValidatorClass = generator.generate();

        ObjenesisStd objenesisStd = new ObjenesisStd();
        Object asmValidator = objenesisStd.newInstance(asmValidatorClass);

        return (AsmValidator) asmValidator;
    }

    public static boolean createValidators(Method method) {
        if (!method.isAnnotationPresent(AsmValid.class)) return false;

        Class<?>[] parameterTypes = method.getParameterTypes();
        String validatorSignature = createValidatorSignature(method);

        for (int i = 0, ii = parameterTypes.length; i < ii; ++i) {
            AsmValidator asmValidator = asmCreate(method, i);
            cache.put(createKey(validatorSignature, i), asmValidator);
        }
        return true;
    }

    public static String createValidatorSignature(Method method) {
        String sig = Asms.sig(method.getReturnType(), method.getParameterTypes());
        String methodValidatorSignature =
                method.getDeclaringClass().getName()
                        + StringUtils.capitalize(method.getName())
                        + UnsignedInts.toString(sig.hashCode());
        return methodValidatorSignature;
    }

    public static void validate(
            String methodSignature, Object... parametersValue) {
        AsmValidateResult result = new AsmValidateResult();
        for (int i = 0, ii = parametersValue.length; i < ii; ++i) {
            AsmValidator validator = getValidator(methodSignature, i);
            if (validator == null) continue;

            AsmValidateResult pResult = validator.validate(parametersValue[i]);
            result.addErrors(pResult);
        }

        result.throwExceptionIfError();
    }
}
