package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import org.objectweb.asm.MethodVisitor;

public interface AsmValidateGenerator {
    void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot,
            LocalIndices localIndices,
            String message);

}
