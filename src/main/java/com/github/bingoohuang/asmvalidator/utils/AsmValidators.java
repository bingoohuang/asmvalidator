package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmMessage;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.utils.lang.Fucks;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.findAnn;
import static org.objectweb.asm.Opcodes.*;

public class AsmValidators {
    public static void addError(
            String name, MethodVisitor mv,
            AnnotationAndRoot annAndRoot,
            String message,
            LocalIndices localIndices,
            Label label
    ) {
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(NEW, Asms.p(ValidateError.class));
        mv.visitInsn(DUP);

        mv.visitLdcInsn(name);
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitLdcInsn(createMessage(annAndRoot, message));
        mv.visitMethodInsn(INVOKESPECIAL, Asms.p(ValidateError.class), "<init>",
                Asms.sig(void.class, String.class, String.class, String.class), false);
        mv.visitMethodInsn(INVOKEVIRTUAL, Asms.p(AsmValidateResult.class), "addError",
                Asms.sig(AsmValidateResult.class, ValidateError.class), false);
        mv.visitInsn(POP); // Pop the unused result of addError
        mv.visitInsn(RETURN);
        mv.visitLabel(label);
    }

    private static String createMessage(
            AnnotationAndRoot annAndRoot,  String message) {
        if (StringUtils.isNotEmpty(message)) return message;

        return parseAsmConstraintMsg(annAndRoot);
    }

    private static String parseAsmConstraintMsg(AnnotationAndRoot annAndRoot) {
        Annotation root = annAndRoot.root();
        Class<? extends Annotation> annClass = root.annotationType();
        AsmConstraint asmConstraint = annClass.getAnnotation(AsmConstraint.class);
        String parsedMessage = asmConstraint.message();
        Method[] methods = root.annotationType().getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            String value = invoke(method, root);

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

    public static void processWideLocal(Class<?> type, LocalIndices localIndices) {
        if (type == long.class) {
            localIndices.incrementLocalIndex();
        }
    }

    public static String tryGetAsmMessage(Annotation[] targetAnns) {
        AsmMessage asmMessage = findAnn(targetAnns, AsmMessage.class);
        return asmMessage != null ? asmMessage.value() : "";
    }
}
