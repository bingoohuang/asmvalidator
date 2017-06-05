package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmMessage;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.utils.lang.Fucks;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.findAnn;
import static org.objectweb.asm.Opcodes.*;

@UtilityClass
public class AsmValidators {
    public static void addError(
            String name,
            Class<?> fieldType,
            MethodVisitor mv,
            AnnotationAndRoot annAndRoot,
            String message,
            LocalIndices localIndices,
            Label label) {
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(NEW, Asms.p(ValidateError.class));
        mv.visitInsn(DUP);

        mv.visitLdcInsn(name);
        mv.visitVarInsn(Asms.loadOpCode(fieldType), localIndices.getOriginalLocalIndex());
        Asms.wrapPrimitive(mv, fieldType);
        mv.visitLdcInsn(createMessage(annAndRoot, message));
        mv.visitMethodInsn(INVOKESPECIAL, Asms.p(ValidateError.class), "<init>",
                Asms.sig(void.class, String.class, Object.class, String.class), false);
        mv.visitMethodInsn(INVOKEVIRTUAL, Asms.p(AsmValidateResult.class), "addError",
                Asms.sig(AsmValidateResult.class, ValidateError.class), false);
        mv.visitInsn(POP); // Pop the unused result of addError
        mv.visitInsn(RETURN);
        mv.visitLabel(label);
    }

    private static String createMessage(AnnotationAndRoot annAndRoot, String message) {
        if (StringUtils.isNotEmpty(message)) return message;

        return parseAsmConstraintMsg(annAndRoot);
    }

    private static String parseAsmConstraintMsg(AnnotationAndRoot annAndRoot) {
        val root = annAndRoot.root();
        val asmConstraint = root.annotationType().getAnnotation(AsmConstraint.class);
        String parsedMessage = asmConstraint.message();
        val methods = root.annotationType().getDeclaredMethods();
        for (val method : methods) {
            String methodName = method.getName();
            String value = "" + invoke(method, root);

            parsedMessage = parsedMessage.replace("{" + methodName + "}", value);
        }

        return parsedMessage;
    }

    public static Object annAttr(Annotation annotation, String methodName) {
        try {
            val method = annotation.annotationType().getDeclaredMethod(methodName);
            return invoke(method, annotation);
        } catch (NoSuchMethodException e) {
            throw Fucks.fuck(e);
        }
    }

    @SneakyThrows
    private static Object invoke(Method method, Object annotation) {
        return method.invoke(annotation);
    }

    public static void processWideLocal(Class<?> type, LocalIndices localIndices) {
        if (type == long.class || type == double.class) {
            localIndices.incrementLocalIndex();
        }
    }

    public static String tryGetAsmMessage(Annotation[] targetAnns) {
        val asmMessage = findAnn(targetAnns, AsmMessage.class);
        return asmMessage != null ? asmMessage.value() : "";
    }

    public static boolean isCollectionAndItemAsmValid(
            Class<?> targetType, Type targetGenericType) {
        if (!Collection.class.isAssignableFrom(targetType)) return false;

        if (!(targetGenericType instanceof ParameterizedType)) return false;

        val pType = (ParameterizedType) targetGenericType;
        val itemType = (Class<?>) pType.getActualTypeArguments()[0];
        return itemType.isAnnotationPresent(AsmValid.class);
    }

    public static Class getCollectionItemClass(Field field) {
        Type genericType = field.getGenericType();

        return getCollectionItemClass(genericType);
    }

    public static Class getCollectionItemClass(Type genericType) {
        val pType = (ParameterizedType) genericType;
        return (Class) pType.getActualTypeArguments()[0];
    }

    public static void computeSize(MethodVisitor mv, Class<?> fieldType, LocalIndices localIndices) {
        if (Collection.class.isAssignableFrom(fieldType)) {
            mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
            mv.visitMethodInsn(INVOKEINTERFACE, p(fieldType), "size", "()I", true);
        } else {
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "length", "()I", false);
        }
    }

    public static Label checkBlankStart(boolean checkBlank, MethodVisitor mv, LocalIndices localIndices) {
        if (!checkBlank) return null;

        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitMethodInsn(INVOKESTATIC, p(StringUtils.class),
                "isBlank", sig(boolean.class, CharSequence.class), false);
        val l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        return l0;
    }

    public static void checkBlankEnd(boolean checkBlank, MethodVisitor mv, Label l0) {
        if (!checkBlank) return;

        mv.visitInsn(RETURN);
        mv.visitLabel(l0);
    }
}
