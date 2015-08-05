package com.github.bingoohuang.asmvalidator;

import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public interface AsmValidationGenerator {
    void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation,
                     int originalLocalIndex, int stringLocalIndex, AtomicInteger localIndex);
}
