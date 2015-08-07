package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmMaxSize;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public class AsmMaxSizeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, Field field,
            Annotation fieldAnnotation, LocalIndices localIndices) {
        AsmMaxSize asmMaxSize = (AsmMaxSize) fieldAnnotation;

        mv.visitVarInsn(ILOAD, localIndices.getStringLocalNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "length", "()I", false);
        int maxSize = asmMaxSize.value();

        Asms.visitInt(mv, maxSize);
        mv.visitJumpInsn(IF_ICMPLE, l1);
        AsmValidators.newValidatorError(mv);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("长度超过" + maxSize);
        AsmValidators.addError(mv);
        mv.visitLabel(l1);
    }

}
