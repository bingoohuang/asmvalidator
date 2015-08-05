package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.ValidatorError;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.asm.Asms.p;
import static com.github.bingoohuang.asmvalidator.asm.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmNotEmptyValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, AtomicInteger localIndex) {
        mv.visitMethodInsn(INVOKESTATIC, p(StringUtils.class), "isBlank",
                sig(boolean.class, CharSequence.class), false);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(NEW, p(ValidatorError.class));
        mv.visitInsn(DUP);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("不能为空");
        mv.visitMethodInsn(INVOKESPECIAL, p(ValidatorError.class), "<init>",
                sig(void.class, String.class, String.class), false);
        mv.visitMethodInsn(INVOKEVIRTUAL, p(AsmValidateResult.class), "addError",
                sig(void.class, ValidatorError.class), false);
        mv.visitLabel(l0);
    }
}
