package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmMaxSize;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public class AsmMaxSizeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, AtomicInteger localIndex) {
        AsmMaxSize asmMaxSize = (AsmMaxSize) fieldAnnotation;

        mv.visitVarInsn(ALOAD, localIndex.get());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        mv.visitVarInsn(ALOAD, localIndex.get());
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
