package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmMobileValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            Annotation fieldAnnotation, LocalIndices localIndices,
            AsmConstraint constraint, String message) {
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitLdcInsn("^1\\d{10}$");
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class),
                "matches", sig(boolean.class, String.class), false);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);

        addError(fieldName, mv, fieldAnnotation, constraint, message, localIndices);
        mv.visitLabel(l0);
    }
}
