package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import lombok.val;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmDateTimeValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv,
            String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot,
            LocalIndices localIndices,
            String message,
            boolean checkBlank
    ) {
        mv.visitVarInsn(ILOAD, localIndices.getIsNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);

        mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
        mv.visitTypeInsn(CHECKCAST, p(String.class));
        mv.visitLdcInsn(AsmValidators.annAttr(annAndRoot.ann(), "format"));
        mv.visitMethodInsn(INVOKESTATIC, p(AsmDateTimeValidateGenerator.class),
                "isDateTimeInFormat",
                sig(boolean.class, String.class, String.class), false);

        mv.visitJumpInsn(IFNE, l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l1);
    }

    public static boolean isDateTimeInFormat(String time, String format) {
        val dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(time);
            return time.equals(dateFormat.format(date));
        } catch (ParseException ex) {
            return false;
        }
    }
}
