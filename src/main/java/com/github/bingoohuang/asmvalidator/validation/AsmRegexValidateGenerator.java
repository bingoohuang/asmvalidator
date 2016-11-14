package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmRegex;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateBadArgException;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmRegexValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot, LocalIndices localIndices,
            String message
    ) {
        AsmRegex asmRegex = (AsmRegex) annAndRoot.ann();
        String regex = asmRegex.value();

        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new AsmValidateBadArgException("正则表达式错误:" + annAndRoot);
        }

        mv.visitVarInsn(ILOAD, localIndices.getIsNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitLdcInsn(regex);
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "matches",
                sig(boolean.class, String.class), false);
        mv.visitJumpInsn(IFNE, l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l1);
    }
}
