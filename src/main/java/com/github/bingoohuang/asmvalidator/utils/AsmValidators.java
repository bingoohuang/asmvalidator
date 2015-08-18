package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.utils.lang.Fucks;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.objectweb.asm.Opcodes.*;

public class AsmValidators {


    public static void addError(
            String name, MethodVisitor mv,
            Annotation fieldAnnotation,
            AsmConstraint constraint, String message,
            LocalIndices localIndices,
            Label label
    ) {
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(NEW, Asms.p(ValidateError.class));
        mv.visitInsn(DUP);

        mv.visitLdcInsn(name);
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitLdcInsn(isEmpty(message) ?
                createMessage(fieldAnnotation, constraint.message()) : message);
        mv.visitMethodInsn(INVOKESPECIAL, Asms.p(ValidateError.class), "<init>",
                Asms.sig(void.class, String.class, String.class, String.class), false);
        mv.visitMethodInsn(INVOKEVIRTUAL, Asms.p(AsmValidateResult.class), "addError",
                Asms.sig(void.class, ValidateError.class), false);
        mv.visitInsn(RETURN);
        mv.visitLabel(label);
    }

    public static String createMessage(
            Annotation annotation, String message) {
        String parsedMessage = message;
        Method[] methods = annotation.annotationType().getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            String value = invoke(method, annotation);

            parsedMessage = parsedMessage.replace("{" + methodName + "}", value);
        }

        return parsedMessage;
    }

    private static String invoke(Method method, Object annotation) {
        try {
            return "" + method.invoke(annotation);
        } catch (Exception e) {
            throw Fucks.fuck(e);
        }
    }
}
