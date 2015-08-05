package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmMinSize;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public class AsmMinSizeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, AtomicInteger localIndex) {
        AsmMinSize asmMinSize = (AsmMinSize) fieldAnnotation;

        mv.visitVarInsn(ALOAD, localIndex.get());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        mv.visitVarInsn(ALOAD, localIndex.get());
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "length", "()I", false);

        int minSize = asmMinSize.value();
        Asms.visitInt(mv, minSize);
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l2);
        mv.visitLabel(l1);
        AsmValidators.newValidatorError(mv);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("长度小于" + minSize);
        AsmValidators.addError(mv);
        mv.visitLabel(l2);
    }

}
