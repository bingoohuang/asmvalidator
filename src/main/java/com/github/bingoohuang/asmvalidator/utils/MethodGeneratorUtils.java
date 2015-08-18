package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.annotations.*;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateBadUsageException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.objectweb.asm.Opcodes.*;

public class MethodGeneratorUtils {
    public static List<Annotation> createValidateAnns(
            Annotation[] targetAnnotations
    ) {
        List<Annotation> asmConstraintsAnns = Lists.newArrayList();
        searchConstraints(asmConstraintsAnns, targetAnnotations);

        // use default not empty and max size validator
        Method defaultMethod = getAsmDefaultAnnotations();
        if (asmConstraintsAnns.isEmpty())
            return Arrays.asList(defaultMethod.getAnnotations());

        tryAddAsmNotBlank(asmConstraintsAnns, defaultMethod);
        tryAddAsmMaxSize(asmConstraintsAnns, defaultMethod);

        return asmConstraintsAnns;
    }

    private static Method getAsmDefaultAnnotations() {
        return AsmDefaultAnnotations.class.getMethods()[0];
    }

    private static void tryAddAsmMaxSize(
            List<Annotation> asmConstraintsAnns, Method defaultMethod) {
        for (Annotation ann : asmConstraintsAnns) {
            if (ann.annotationType() == AsmMaxSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        asmConstraintsAnns.add(0, defaultMethod.getAnnotation(AsmMaxSize.class));
    }

    private static void tryAddAsmNotBlank(
            List<Annotation> asmConstraintsAnns, Method defaultMethod) {
        for (Annotation ann : asmConstraintsAnns) {
            if (ann.annotationType() == AsmBlankable.class) return;
            if (ann.annotationType() == AsmMinSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        asmConstraintsAnns.add(0, defaultMethod.getAnnotation(AsmNotBlank.class));
    }

    public static void searchAnnotations(
            List<Annotation> asmConstraints, Annotation annotation) {
        Annotation[] annotations = annotation.annotationType().getAnnotations();
        searchConstraints(asmConstraints, annotations);
    }

    public static void searchConstraints(
            List<Annotation> asmConstraintAnns, Annotation[] annotations) {
        for (Annotation ann : annotations) {
            Class<?> annType = ann.annotationType();
            AsmConstraint asmConstraint = annType.getAnnotation(AsmConstraint.class);
            if (asmConstraint == null) continue;

            searchAnnotations(asmConstraintAnns, ann);
            asmConstraintAnns.add(ann);
        }
    }

    public static MethodVisitor startFieldValidatorMethod(
            ClassWriter cw,
            String fieldName,
            Class beanClass
    ) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE,
                "validate" + StringUtils.capitalize(fieldName),
                sig(void.class, beanClass, AsmValidateResult.class),
                null, null);
        mv.visitCode();
        return mv;
    }

    public static void endFieldValidateMethod(MethodVisitor mv) {
        mv.visitInsn(RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    public static boolean isAnnotationPresent(
            Annotation[] targetAnnotations,
            Class<?> annotationType) {
        for (Annotation ann : targetAnnotations) {
            if (annotationType.isInstance(ann)) return true;
        }

        return false;
    }

    public static  <T> T findAnn(
            Annotation[] targetAnnotations,
            Class<T> annotationType) {
        for (Annotation ann : targetAnnotations) {
            if (annotationType.isInstance(ann)) return (T) ann;
        }

        return null;
    }

    public static void visitGetter(MethodVisitor mv, Field field) {
        String getterName = "get" + capitalize(field.getName());
        Class<?> declaringClass = field.getDeclaringClass();
        try {
            declaringClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            throw new AsmValidateBadUsageException(
                    "there is no getter method for field "
                            + field.getName());
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, p(declaringClass),
                getterName, sig(field.getType()), false);
    }

    public static void addIsStringNullLocal(
            LocalIndices localIndices, MethodVisitor mv) {
        Label l0 = new Label();
        mv.visitJumpInsn(IFNONNULL, l0);
        mv.visitInsn(ICONST_1);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l1);
        localIndices.incrementAndSetStringNullLocalIndex();
        mv.visitVarInsn(ISTORE, localIndices.getLocalIndex());
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
    }

    public static void createBridge(
            ClassWriter cw, Class beanClass, String implName) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "validate",
                sig(AsmValidateResult.class, Object.class), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, p(beanClass));
        mv.visitMethodInsn(INVOKEVIRTUAL, p(implName), "validate",
                sig(AsmValidateResult.class, beanClass), false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    public static void visitValidateFieldMethod(
            MethodVisitor mv, String implName, String fieldName, Class fieldClass) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, p(implName),
                "validate" + capitalize(fieldName),
                sig(void.class, fieldClass, AsmValidateResult.class), false);
    }
}
