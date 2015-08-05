package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmRegex;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmRegexValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation,
                            int originalLocalIndex, int stringLocalIndex, AtomicInteger localIndex) {
        AsmRegex asmRegex = (AsmRegex) fieldAnnotation;
        String regex = asmRegex.value();

        mv.visitVarInsn(ALOAD, stringLocalIndex);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        mv.visitVarInsn(ALOAD, stringLocalIndex);
        mv.visitLdcInsn(regex);
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "matches",
                sig(boolean.class, String.class), false);
        mv.visitJumpInsn(IFNE, l1);
        AsmValidators.newValidatorError(mv);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("格式错误");
        AsmValidators.addError(mv);
        mv.visitLabel(l1);
    }
}
