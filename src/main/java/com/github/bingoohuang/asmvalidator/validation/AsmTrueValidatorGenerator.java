package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmTypeValidateGenerator;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmTrueValidatorGenerator implements AsmTypeValidateGenerator {
    @Override
    public boolean supportClass(Class<?> clazz) {
        return String.class == clazz
                || clazz == boolean.class
                || clazz == Boolean.class;
    }

    @Override
    public void generateAsm(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            AnnotationAndRoot annAndRoot,
            LocalIndices localIndices,
            String message
    ) {
        mv.visitVarInsn(ILOAD, localIndices.getIsNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);

        if (String.class == fieldType) {
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            mv.visitLdcInsn(useRegex());
            mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "matches",
                    sig(boolean.class, String.class), false);

        } else if (Boolean.class == fieldType || boolean.class == fieldType) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, p(Boolean.class));
            mv.visitMethodInsn(INVOKEVIRTUAL, p(Boolean.class),
                    "booleanValue", "()Z", false);
        }

        mv.visitJumpInsn(IFNE, l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l1);
    }

    protected String useRegex() {
        return "(?)true|yes|on|ok";
    }
}
