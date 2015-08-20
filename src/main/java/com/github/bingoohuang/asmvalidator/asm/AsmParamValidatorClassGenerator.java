package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.annotations.AsmCreateClassFile4Debug;

import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;

public class AsmParamValidatorClassGenerator
        extends AsmBaseValidatorClassGenerator {
    private final Method targetMethod;
    private final int targetParameterIndex;

    public AsmParamValidatorClassGenerator(
            Method targetMethod, int targetParameterIndex) {
        super(createValidatorSignature(targetMethod) + "$"
                + targetParameterIndex + "AsmParamValidator$BINGOOASM$Impl");
        this.targetMethod = targetMethod;
        this.targetParameterIndex = targetParameterIndex;
    }

    protected boolean createClassFileForDiagnose() {
        return targetMethod.isAnnotationPresent(AsmCreateClassFile4Debug.class);
    }

    @Override
    protected AsmValidatorMethodGeneratable getAsmValidatorMethodGeneratable() {
        return new AsmParamValidatorMethodGenerator(implName,
                targetMethod, targetParameterIndex, classWriter);
    }
}
