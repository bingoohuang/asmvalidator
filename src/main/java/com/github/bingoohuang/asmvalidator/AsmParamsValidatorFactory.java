package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.asm.AsmParamValidatorClassGenerator;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.primitives.UnsignedInts;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;

@UtilityClass
public class AsmParamsValidatorFactory {
    private Cache<String, AsmValidator>
            cache = CacheBuilder.newBuilder().build();


    public AsmValidator getValidator(
            String methodSignature, int parameterIndex) {
        return cache.getIfPresent(createKey(methodSignature, parameterIndex));
    }

    private String createKey(String methodSig, int parameterIndex) {
        return methodSig + "." + parameterIndex;
    }

    private AsmValidator asmCreate(Method method, int parameterIndex) {
        val generator = new AsmParamValidatorClassGenerator(method, parameterIndex);
        Class<?> asmValidatorClass = generator.generate();

        ObjenesisStd objenesisStd = new ObjenesisStd();
        Object asmValidator = objenesisStd.newInstance(asmValidatorClass);

        return (AsmValidator) asmValidator;
    }

    public boolean createValidators(Method method) {
        if (!method.isAnnotationPresent(AsmValid.class)) return false;

        Class<?>[] parameterTypes = method.getParameterTypes();
        String validatorSignature = createValidatorSignature(method);

        for (int i = 0, ii = parameterTypes.length; i < ii; ++i) {
            String key = createKey(validatorSignature, i);
            if (cache.getIfPresent(key) == null) {
                AsmValidator asmValidator = asmCreate(method, i);
                cache.put(key, asmValidator);
            } else {
                return true;
            }
        }
        return true;
    }

    public String createValidatorSignature(Method method) {
        String sig = sig(method.getReturnType(), method.getParameterTypes());
        String methodValidatorSignature =
                method.getDeclaringClass().getName()
                        + StringUtils.capitalize(method.getName())
                        + UnsignedInts.toString(sig.hashCode());
        return methodValidatorSignature;
    }

    public void validate(
            Method method, int parameterIndex, Object parameterValue) {

        String methodSignature = createValidatorSignature(method);
        String key = createKey(methodSignature, parameterIndex);
        AsmValidator validator = cache.getIfPresent(key);
        AsmValidateResult result = validator.validate(parameterValue);
        result.throwExceptionIfError();
    }

    public void validate(
            String methodSignature, Object... parametersValues) {
        AsmValidateResult result = new AsmValidateResult();
        for (int i = 0, ii = parametersValues.length; i < ii; ++i) {
            AsmValidator validator = getValidator(methodSignature, i);
            if (validator == null) continue;

            Object parametersValue = parametersValues[i];
            result.addErrors(validator.validate(parametersValue));
        }

        result.throwExceptionIfError();
    }
}
