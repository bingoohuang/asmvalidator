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
import java.util.List;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.objectweb.asm.Opcodes.*;

public class MethodGeneratorUtils {
    public static List<AnnotationAndRoot> createValidateAnns(
            Annotation[] targetAnnotations,
            Class<?> type) {
        List<AnnotationAndRoot> asmConstraintsAnns = Lists.newArrayList();
        searchConstraints(asmConstraintsAnns, targetAnnotations);

        // use default not empty and max size validator
        Method defaultMethod = getAsmDefaultAnnotations();
        if (asmConstraintsAnns.isEmpty())
            return filterForPrimitiveType(defaultMethod.getAnnotations(), type);

        tryAddAsmMaxSize(asmConstraintsAnns, defaultMethod);
        tryAddAsmNotBlank(asmConstraintsAnns, defaultMethod, type);

        return asmConstraintsAnns;
    }

    private static List<AnnotationAndRoot> filterForPrimitiveType(
            Annotation[] annotations, Class<?> type) {
        List<AnnotationAndRoot> result = Lists.newArrayList();

        for (Annotation ann : annotations) {
            if (ann instanceof AsmNotBlank
                    && type.isPrimitive()) continue;
            result.add(new AnnotationAndRoot(ann, null));
        }

        return result;
    }

    private static Method getAsmDefaultAnnotations() {
        return AsmDefaultAnnotations.class.getMethods()[0];
    }

    private static void tryAddAsmMaxSize(
            List<AnnotationAndRoot> asmConstraintsAnns, Method defaultMethod) {
        for (AnnotationAndRoot annAndRoot : asmConstraintsAnns) {
            Annotation ann = annAndRoot.ann();
            if (ann.annotationType() == AsmMaxSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        AsmMaxSize ann = defaultMethod.getAnnotation(AsmMaxSize.class);
        asmConstraintsAnns.add(0, new AnnotationAndRoot(ann, null));
    }

    private static void tryAddAsmNotBlank(
            List<AnnotationAndRoot> asmConstraintsAnns,
            Method defaultMethod, Class<?> type) {
        if (type.isPrimitive()) return;

        for (AnnotationAndRoot annAndRoot : asmConstraintsAnns) {
            Annotation ann = annAndRoot.ann();
            if (ann.annotationType() == AsmBlankable.class) return;
            if (ann.annotationType() == AsmMinSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        AsmNotBlank ann = defaultMethod.getAnnotation(AsmNotBlank.class);
        asmConstraintsAnns.add(0, new AnnotationAndRoot(ann, null));
    }


    public static void searchConstraints(
            List<AnnotationAndRoot> asmConstraintAnns,
            Annotation[] annotations
    ) {
        searchConstraints(asmConstraintAnns, annotations, null);
    }

    public static void searchConstraints(
            List<AnnotationAndRoot> asmConstraintAnns,
            Annotation[] annotations,
            Annotation rootAnnotation
    ) {

        for (Annotation ann : annotations) {
            Class<?> annType = ann.annotationType();
            AsmConstraint asmConstraint = annType.getAnnotation(AsmConstraint.class);
            if (asmConstraint == null) continue;

            Annotation[] subAnns = ann.annotationType().getAnnotations();
            Annotation rootAnn = rootAnnotation == null ? ann : rootAnnotation;
            searchConstraints(asmConstraintAnns, subAnns, rootAnn);
            asmConstraintAnns.add(new AnnotationAndRoot(ann, rootAnn));
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

    public static <T> T findAnn(
            Annotation[] targetAnnotations,
            Class<T> annotationType) {
        for (Annotation ann : targetAnnotations) {
            if (annotationType.isInstance(ann)) return (T) ann;
        }

        return null;
    }

    public static void visitGetter(MethodVisitor mv, Field field) {
        mv.visitVarInsn(ALOAD, 1);
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
