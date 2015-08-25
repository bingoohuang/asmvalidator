package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmBase64;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmBase64ValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv,
            String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot, LocalIndices localIndices,
            String message) {
        int stringLocalIndex = localIndices.getStringLocalIndex();

        mv.visitVarInsn(ALOAD, stringLocalIndex);
        int paddedIndex = localIndices.incrementLocalIndex();
        mv.visitVarInsn(ASTORE, paddedIndex);

        AsmBase64 asmBase64 = (AsmBase64) annAndRoot.ann();
        if (asmBase64.purified()) {
            mv.visitVarInsn(ALOAD, stringLocalIndex);
            mv.visitMethodInsn(INVOKESTATIC,
                    p(AsmBase64ValidateGenerator.class),
                    "padding", sig(String.class, String.class), false);
            mv.visitVarInsn(ASTORE, paddedIndex);
        }

        mv.visitVarInsn(ILOAD, localIndices.getStringLocalNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitVarInsn(ALOAD, paddedIndex);
        // URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548)
        mv.visitLdcInsn(asmBase64.format() == AsmBase64.Base64Format.Standard
                ? "^(?:[\\w+/]{4})*(?:[\\w+/]{2}==|[\\w+/]{3}=)?$"
                : "^(?:[\\w\\-_]{4})*(?:[\\w\\-_]{2}==|[\\w\\-_]{3}=)?$");
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "matches",
                sig(boolean.class, String.class), false);
        mv.visitJumpInsn(IFNE, l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l1);
    }

    public static String padding(String str) {
        int length = str.length();
        int padding = length % 4;
        if (padding == 0) return str;

        return StringUtils.rightPad(str, length + 4 - padding, '=');
    }
}
