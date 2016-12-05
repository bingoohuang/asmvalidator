package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.asm.AsmParamValidatorClassGenerator;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.primitives.UnsignedInts;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;

public class AsmParamsValidatorFactory {
    private static Cache<String, AsmValidator<Object>>
            cache = CacheBuilder.newBuilder().build();


    public static AsmValidator<Object> getValidator(
            String methodSignature, int parameterIndex) {
        return cache.getIfPresent(createKey(methodSignature, parameterIndex));
    }

    private static String createKey(String methodSig, int parameterIndex) {
        return methodSig + "." + parameterIndex;
    }

    @SuppressWarnings("unchecked")
    private static AsmValidator<Object> asmCreate(Method method, int parameterIndex) {
        val generator = new AsmParamValidatorClassGenerator(method, parameterIndex);
        val asmValidatorClass = generator.generate();

        val objenesisStd = new ObjenesisStd();
        val asmValidator = objenesisStd.newInstance(asmValidatorClass);

        return (AsmValidator<Object>) asmValidator;
    }

    public static boolean createValidators(Method method) {
        if (!method.isAnnotationPresent(AsmValid.class)) return false;

        val parameterTypes = method.getParameterTypes();
        val validatorSignature = createValidatorSignature(method);

        for (int i = 0, ii = parameterTypes.length; i < ii; ++i) {
            val key = createKey(validatorSignature, i);
            if (cache.getIfPresent(key) == null) {
                val asmValidator = asmCreate(method, i);
                cache.put(key, asmValidator);
            } else {
                return true;
            }
        }
        return true;
    }

    public static String createValidatorSignature(Method method) {
        String sig = sig(method.getReturnType(), method.getParameterTypes());
        String methodValidatorSignature =
                method.getDeclaringClass().getName()
                        + StringUtils.capitalize(method.getName())
                        + UnsignedInts.toString(sig.hashCode());
        return methodValidatorSignature;
    }

    public static void validate(
            Method method, int parameterIndex, Object parameterValue) {

        val methodSignature = createValidatorSignature(method);
        val key = createKey(methodSignature, parameterIndex);
        val validator = cache.getIfPresent(key);
        val result = validator.validate(parameterValue);
        result.throwExceptionIfError();
    }

    public static void validate(
            String methodSignature, Object... parametersValues) {
        val result = new AsmValidateResult();
        for (int i = 0, ii = parametersValues.length; i < ii; ++i) {
            val validator = getValidator(methodSignature, i);
            if (validator == null) continue;

            val parametersValue = parametersValues[i];
            result.addErrors(validator.validate(parametersValue));
        }

        result.throwExceptionIfError();
    }
}
