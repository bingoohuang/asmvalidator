package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class AsmNotEmptyValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, LocalIndices localIndices) {
        mv.visitMethodInsn(INVOKESTATIC, p(StringUtils.class), "isBlank",
                sig(boolean.class, CharSequence.class), false);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        AsmValidators.newValidatorError(mv);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("不能为空");
        AsmValidators.addError(mv);
        mv.visitLabel(l0);
    }
}
