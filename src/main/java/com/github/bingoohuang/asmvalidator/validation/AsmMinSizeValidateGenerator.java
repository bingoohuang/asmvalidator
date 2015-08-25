package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmMinSize;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.Collection;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public class AsmMinSizeValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot, LocalIndices localIndices,
            String message) {
        AsmMinSize asmMinSize = (AsmMinSize) annAndRoot.ann();

        mv.visitVarInsn(ILOAD, localIndices.getStringLocalNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);

        if (Collection.class.isAssignableFrom(fieldType)) {
            mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
            mv.visitMethodInsn(INVOKEINTERFACE, p(fieldType), "size", "()I", true);
        } else {
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "length", "()I", false);
        }

        int minSize = asmMinSize.value();
        Asms.visitInt(mv, minSize);
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l2);
        mv.visitLabel(l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l2);
    }
}
