package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmConstraintCache;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils;
import com.google.common.primitives.UnsignedInts;
import lombok.val;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.findMsaSupportType;
import static org.objectweb.asm.Opcodes.*;

public class AsmCustomValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName,
            Class<?> fieldType, Type genericFieldType, AnnotationAndRoot annAndRoot,
            LocalIndices localIndices,
            String message,
            boolean checkBlank
    ) {
        val annHashCode = UnsignedInts.toString(annAndRoot.hashCode());
        val annType = annAndRoot.ann().annotationType();
        val hashCode = annType.getName() + ":" + annHashCode;
        AsmConstraintCache.put(hashCode, annAndRoot.ann());

        val constraint = annType.getAnnotation(AsmConstraint.class);
        val msaSupportType = findMsaSupportType(constraint, fieldType);

        Label l0 = AsmValidators.checkBlankStart(checkBlank, mv, localIndices, fieldType);

        mv.visitLdcInsn(hashCode);
        mv.visitMethodInsn(INVOKESTATIC, p(AsmConstraintCache.class), "get",
                sig(Annotation.class, String.class), false);
        mv.visitTypeInsn(CHECKCAST, p(annType));
        int castedAnn = localIndices.incrementLocalIndex();
        mv.visitVarInsn(ASTORE, castedAnn);
        mv.visitTypeInsn(NEW, p(msaSupportType));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(msaSupportType),
                "<init>", "()V", false);
        int customValidator = localIndices.incrementLocalIndex();
        mv.visitVarInsn(ASTORE, customValidator);
        mv.visitVarInsn(ALOAD, customValidator);
        mv.visitVarInsn(ALOAD, castedAnn);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
        mv.visitTypeInsn(CHECKCAST, p(fieldType));
        mv.visitMethodInsn(INVOKEVIRTUAL, p(msaSupportType),
                MethodGeneratorUtils.VALIDATE, sig(void.class, annType,
                        AsmValidateResult.class, fieldType), false);

        AsmValidators.checkBlankEnd(mv, l0);
    }


}
