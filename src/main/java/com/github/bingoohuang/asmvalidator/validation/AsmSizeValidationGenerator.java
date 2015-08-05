package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmSize;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public class AsmSizeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation,
                            int originalLocalIndex, int stringLocalIndex, AtomicInteger localIndex) {
        AsmSize asmSize = (AsmSize) fieldAnnotation;

        mv.visitVarInsn(ALOAD, stringLocalIndex);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        mv.visitVarInsn(ALOAD, stringLocalIndex);
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "length", "()I", false);

        int size = asmSize.value();
        Asms.visitInt(mv, size);
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l2);
        mv.visitLabel(l1);
        AsmValidators.newValidatorError(mv);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("长度不等于" + size);
        AsmValidators.addError(mv);
        mv.visitLabel(l2);
    }
}
