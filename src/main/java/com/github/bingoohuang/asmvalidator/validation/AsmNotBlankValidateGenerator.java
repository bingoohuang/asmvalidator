package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Type;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmNotBlankValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            Type genericFieldType, AnnotationAndRoot annAndRoot, LocalIndices localIndices,
            String message,
            boolean checkBlank) {
        if (CharSequence.class.isAssignableFrom(fieldType)) {
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            mv.visitMethodInsn(INVOKESTATIC, p(StringUtils.class), "isBlank",
                    sig(boolean.class, CharSequence.class), false);
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l0);
        } else {
            mv.visitVarInsn(ILOAD, localIndices.getIsNullIndex());
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l0);
        }
    }
}
