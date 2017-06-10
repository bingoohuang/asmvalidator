package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Type;

public interface AsmValidateGenerator {
    void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            Type genericFieldType, AnnotationAndRoot annAndRoot,
            LocalIndices localIndices,
            String message,
            boolean checkBlank);

}
