package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;

public class AsmNoopValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            Annotation fieldAnnotation,
            LocalIndices localIndices, AsmConstraint constraint, String message) {
    }
}
