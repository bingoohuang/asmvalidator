package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.ValidatorError;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class AsmValidators {
    public static void addError(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESPECIAL, Asms.p(ValidatorError.class), "<init>",
                Asms.sig(void.class, String.class, String.class), false);
        mv.visitMethodInsn(INVOKEVIRTUAL, Asms.p(AsmValidateResult.class), "addError",
                Asms.sig(void.class, ValidatorError.class), false);
        mv.visitInsn(RETURN);
    }

    public static void newValidatorError(MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(NEW, Asms.p(ValidatorError.class));
        mv.visitInsn(DUP);
    }
}
