package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface AsmValidationGenerator {
    void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, LocalIndices localIndices);

}
