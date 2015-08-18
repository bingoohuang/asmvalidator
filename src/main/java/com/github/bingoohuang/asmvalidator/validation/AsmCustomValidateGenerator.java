package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AsmConstraintCache;
import com.google.common.primitives.UnsignedInts;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmCustomValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName,
            Class<?> fieldType, Annotation fieldAnn,
            LocalIndices localIndices, AsmConstraint constraint,
            String message
    ) {
        String annHashCode = UnsignedInts.toString(fieldAnn.hashCode());
        String hashCode = fieldAnn.annotationType().getName() + ":" + annHashCode;

        AsmConstraintCache.put(hashCode, fieldAnn);

        mv.visitLdcInsn(hashCode);
        mv.visitMethodInsn(INVOKESTATIC, p(AsmConstraintCache.class), "get",
                sig(Annotation.class, String.class), false);
        mv.visitTypeInsn(CHECKCAST, p(fieldAnn.annotationType()));
        int castedAnn = localIndices.incrementLocalIndex();
        mv.visitVarInsn(ASTORE, castedAnn);
        mv.visitTypeInsn(NEW, p(constraint.validateBy()));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(constraint.validateBy()),
                "<init>", "()V", false);
        int customValidator = localIndices.incrementLocalIndex();
        mv.visitVarInsn(ASTORE, customValidator);
        mv.visitVarInsn(ALOAD, customValidator);
        mv.visitVarInsn(ALOAD, castedAnn);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
        mv.visitTypeInsn(CHECKCAST, p(fieldType));
        mv.visitMethodInsn(INVOKEVIRTUAL, p(constraint.validateBy()),
                "validate", sig(void.class, fieldAnn.annotationType(),
                        AsmValidateResult.class, fieldType), false);
    }
}
