package com.github.bingoohuang.asmvalidator;

public interface AsmTypeValidateGenerator extends AsmValidateGenerator {
    boolean supportClass(Class<?> clazz);
}
