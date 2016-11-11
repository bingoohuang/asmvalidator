package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.AsmTypeValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.MsaValidator;
import com.github.bingoohuang.asmvalidator.annotations.*;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateBadUsageException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.google.common.primitives.Primitives.wrap;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.objectweb.asm.Opcodes.*;

@Slf4j
public class MethodGeneratorUtils {
    public static List<AnnotationAndRoot> createValidateAnns(
            Annotation[] targetAnnotations,
            Class<?> type) {
        List<AnnotationAndRoot> asmConstraintsAnns = Lists.newArrayList();
        searchConstraints(asmConstraintsAnns, targetAnnotations);

        // use default not empty and max size validator
        Method defaultMethod = getAsmDefaultAnnotations();
        if (asmConstraintsAnns.isEmpty()) {
            Annotation[] annotations = defaultMethod.getAnnotations();
            asmConstraintsAnns = filterForSupportedType(annotations, type);
        } else {
            tryAddAsmMaxSize(asmConstraintsAnns, defaultMethod, type);
            tryAddAsmNotBlank(asmConstraintsAnns, defaultMethod, type);
        }

        List<AnnotationAndRoot> filtered = Lists.newArrayList();
        for (AnnotationAndRoot annAndRoot : asmConstraintsAnns) {
            if (supportType(annAndRoot.ann(), type)) filtered.add(annAndRoot);
            else {
                log.warn("{} not support type {}", annAndRoot.ann(), type);
            }
        }

        return filtered;
    }

    private static List<AnnotationAndRoot> filterForSupportedType(
            Annotation[] annotations, Class<?> type) {
        List<AnnotationAndRoot> result = Lists.newArrayList();

        for (Annotation ann : annotations) {
            if (supportType(ann, type))
                result.add(new AnnotationAndRoot(ann, null));
        }

        return result;
    }

    private static Method getAsmDefaultAnnotations() {
        return AsmDefaultAnnotations.class.getMethods()[0];
    }

    public static boolean supportType(Annotation ann, Class<?> type) {
        val annClass = ann.annotationType();
        val asmConstraint = annClass.getAnnotation(AsmConstraint.class);
        for (Class<?> supportedClass : asmConstraint.supportedClasses()) {
            if (supportedClass.isAssignableFrom(type)) return true;
        }

        if (findMsaSupportType(asmConstraint, type) != null) return true;
        if (asmTypeValidateGeneratorSupport(asmConstraint, type)) return true;

        return false;
    }

    private static boolean asmTypeValidateGeneratorSupport(
            AsmConstraint asmConstraint, Class<?> type
    ) {
        ObjenesisStd objStd = new ObjenesisStd();
        for (Class<? extends AsmValidateGenerator> asmValidateBy
                : asmConstraint.asmValidateBy()) {
            Class<?> clazz = AsmTypeValidateGenerator.class;
            if (!clazz.isAssignableFrom(asmValidateBy)) continue;

            val instance = objStd.newInstance(asmValidateBy);
            val generator = (AsmTypeValidateGenerator) instance;
            if (generator.supportClass(type)) return true;
        }

        return false;
    }

    public static Class<? extends MsaValidator> findMsaSupportType(
            AsmConstraint asmConstraint, Class<?> type
    ) {
        Class<?> wrap = wrap(type);
        for (Class<? extends MsaValidator> msa : asmConstraint.validateBy()) {
            Class<?> arg = findSuperActualTypeArg(msa, MsaValidator.class, 1);
            if (arg == null) continue;
            if (arg.isAssignableFrom(wrap)) return msa;
        }

        return null;
    }

    public static Class<?> findSuperActualTypeArg(
            final Class<?> subClass,
            final Class<?> superClass,
            final int argIndex
    ) {
        for (Type type : subClass.getGenericInterfaces()) {
            if (!(type instanceof ParameterizedType)) continue;

            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType() != superClass) continue;

            Type argType = pType.getActualTypeArguments()[argIndex];
            if (argType instanceof Class) return (Class<?>) argType;
        }

        Class<?> parentClass = subClass.getSuperclass();
        if (parentClass == Object.class) return null;

        return findSuperActualTypeArg(parentClass, superClass, argIndex);
    }

    private static void tryAddAsmMaxSize(
            List<AnnotationAndRoot> asmConstraintsAnns,
            Method defaultMethod,
            Class<?> type
    ) {
        for (AnnotationAndRoot annAndRoot : asmConstraintsAnns) {
            Annotation ann = annAndRoot.ann();
            if (ann.annotationType() == AsmMaxSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        AsmMaxSize ann = defaultMethod.getAnnotation(AsmMaxSize.class);

        if (supportType(ann, type))
            asmConstraintsAnns.add(0, new AnnotationAndRoot(ann, null));
    }

    private static void tryAddAsmNotBlank(
            List<AnnotationAndRoot> asmConstraintsAnns,
            Method defaultMethod, Class<?> type
    ) {
        if (type.isPrimitive()) return;

        for (AnnotationAndRoot annAndRoot : asmConstraintsAnns) {
            Annotation ann = annAndRoot.ann();
            if (ann.annotationType() == AsmNotBlank.class) return;
            if (ann.annotationType() == AsmBlankable.class) return;
            if (ann.annotationType() == AsmMinSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        AsmNotBlank ann = defaultMethod.getAnnotation(AsmNotBlank.class);

        if (supportType(ann, type))
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
            val annType = ann.annotationType();
            val asmConstraint = annType.getAnnotation(AsmConstraint.class);
            if (asmConstraint == null) continue;

            val subAnns = ann.annotationType().getAnnotations();
            val rootAnn = rootAnnotation == null ? ann : rootAnnotation;
            searchConstraints(asmConstraintAnns, subAnns, rootAnn);
            asmConstraintAnns.add(new AnnotationAndRoot(ann, rootAnn));
        }
    }

    public static MethodVisitor startFieldValidatorMethod(
            ClassWriter cw,
            String fieldName,
            Class beanClass
    ) {
        val mv = cw.visitMethod(ACC_PRIVATE,
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
            Class<?> annotationType
    ) {
        for (Annotation ann : targetAnnotations) {
            if (annotationType.isInstance(ann)) return true;
        }

        return false;
    }

    public static <T> T findAnn(
            Annotation[] targetAnnotations,
            Class<T> annotationType
    ) {
        for (Annotation ann : targetAnnotations) {
            if (annotationType.isInstance(ann)) return (T) ann;
        }

        return null;
    }

    public static void visitGetter(MethodVisitor mv, Field field
    ) {
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

        Class<?> type = field.getType();
        mv.visitMethodInsn(INVOKEVIRTUAL, p(declaringClass),
                getterName, sig(type), false);
    }

    public static void addIsNullLocal(
            LocalIndices localIndices, MethodVisitor mv
    ) {
        Label l0 = new Label();
        mv.visitJumpInsn(IFNONNULL, l0);
        mv.visitInsn(ICONST_1);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l1);
        localIndices.incrementAndSetNullLocalIndex();
        mv.visitVarInsn(ISTORE, localIndices.getLocalIndex());
    }

    public static void createBridge(
            ClassWriter cw, Class beanClass, String implName
    ) {
        val mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "validate",
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
            MethodVisitor mv, String implName, String fieldName, Class fieldClass
    ) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, p(implName),
                "validate" + capitalize(fieldName),
                sig(void.class, fieldClass, AsmValidateResult.class), false);
    }
}
