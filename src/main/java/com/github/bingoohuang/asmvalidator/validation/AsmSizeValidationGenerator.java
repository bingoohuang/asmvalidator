package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.ValidatorError;
import com.github.bingoohuang.asmvalidator.annotations.AsmSize;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.asm.Asms.p;
import static com.github.bingoohuang.asmvalidator.asm.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmSizeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, AtomicInteger localIndex) {
        AsmSize asmSize = (AsmSize) fieldAnnotation;

        mv.visitVarInsn(ALOAD, localIndex.get());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        mv.visitVarInsn(ALOAD, localIndex.get());
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "length", "()I", false);
        mv.visitIntInsn(BIPUSH, asmSize.max());
        mv.visitJumpInsn(IF_ICMPLE, l1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(NEW, p(ValidatorError.class));
        mv.visitInsn(DUP);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("长度超过" + asmSize.max());
        mv.visitMethodInsn(INVOKESPECIAL, p(ValidatorError.class), "<init>",
                sig(void.class, String.class, String.class), false);
        mv.visitMethodInsn(INVOKEVIRTUAL, p(AsmValidateResult.class), "addError",
                sig(void.class, ValidatorError.class), false);
        mv.visitLabel(l1);
    }
}
