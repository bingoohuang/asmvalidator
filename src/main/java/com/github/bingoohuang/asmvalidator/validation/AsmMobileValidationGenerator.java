package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmMobileValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, Field field,
            Annotation fieldAnnotation, LocalIndices localIndices) {

        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitLdcInsn("^1\\d{10}$");
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class),
                "matches", sig(boolean.class, String.class), false);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);

        AsmValidators.newValidatorError(mv);

        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("手机号码格式不正确!");
        AsmValidators.addError(mv);
        mv.visitLabel(l0);
    }
}
