package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmPastValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv,
            String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot,
            LocalIndices localIndices,
            String message
    ) {
        mv.visitVarInsn(ILOAD, localIndices.getIsNullIndex());
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitJumpInsn(IFNE, l2);

        mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
        if (Date.class.isAssignableFrom(fieldType)) {
            mv.visitTypeInsn(CHECKCAST, p(Date.class));
            mv.visitMethodInsn(INVOKEVIRTUAL, p(Date.class),
                    "getTime", sig(long.class), false);
        } else if (Calendar.class.isAssignableFrom(fieldType)) {
            mv.visitTypeInsn(CHECKCAST, p(Calendar.class));
            mv.visitMethodInsn(INVOKEVIRTUAL, p(Calendar.class),
                    "getTimeInMillis", sig(long.class), false);
        } else if (String.class == fieldType) {
            mv.visitTypeInsn(CHECKCAST, p(String.class));
            mv.visitLdcInsn(AsmValidators.annAttr(annAndRoot.ann(), "format"));
            mv.visitMethodInsn(INVOKESTATIC, p(AsmPastValidateGenerator.class),
                    "timeInMillis",
                    sig(long.class, String.class, String.class), false);
        }

        mv.visitMethodInsn(INVOKESTATIC, p(System.class),
                "currentTimeMillis", sig(long.class), false);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(compareOpCode(), l2);
        mv.visitLabel(l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l2);
    }

    protected int compareOpCode() {
        return IFLT;
    }

    public static long timeInMillis(String time, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(time).getTime();
    }
}
