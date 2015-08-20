package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.annotations.AsmCreateClassFile4Debug;

public class AsmValidatorClassGenerator
        extends AsmBaseValidatorClassGenerator {
    private final Class<?> beanClass;

    public AsmValidatorClassGenerator(Class<?> beanClass) {
        super(beanClass.getName() + "AsmValidator$BINGOOASM$Impl");
        this.beanClass = beanClass;
    }

    protected boolean createClassFileForDiagnose() {
        return beanClass.isAnnotationPresent(AsmCreateClassFile4Debug.class);
    }

    @Override
    protected AsmValidatorMethodGeneratable getAsmValidatorMethodGeneratable() {
        return new AsmValidatorMethodGenerator(beanClass, classWriter, implName);
    }
}
