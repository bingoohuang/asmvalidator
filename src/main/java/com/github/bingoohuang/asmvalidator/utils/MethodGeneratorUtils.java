package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.AsmTypeValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.MsaValidator;
import com.github.bingoohuang.asmvalidator.annotations.*;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateBadUsageException;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import lombok.experimental.UtilityClass;
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
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.objectweb.asm.Opcodes.*;

@Slf4j @UtilityClass
public class MethodGeneratorUtils {
    public static final String VALIDATE = "validate";

    public static List<AnnotationAndRoot> createValidateAnns(Annotation[] targetAnnotations, Class<?> type) {
        List<AnnotationAndRoot> asmConstraintsAnns = Lists.newArrayList();
        searchConstraints(asmConstraintsAnns, targetAnnotations);

        // use default not empty and max size validator
        val defaultMethod = getAsmDefaultAnnotations();
        if (asmConstraintsAnns.isEmpty()) {
            val annotations = defaultMethod.getAnnotations();
            asmConstraintsAnns = filterForSupportedType(annotations, type);
        } else {
            tryAddAsmMaxSize(asmConstraintsAnns, defaultMethod, type);
            tryAddAsmNotBlank(asmConstraintsAnns, defaultMethod, type);
        }

        List<AnnotationAndRoot> filtered = Lists.newArrayList();
        for (val annAndRoot : asmConstraintsAnns) {
            if (supportType(annAndRoot.ann(), type)) {
                filtered.add(annAndRoot);
            } else {
                log.warn("{} does not support type {}", annAndRoot.ann(), type);
            }
        }

        return filtered;
    }

    private static List<AnnotationAndRoot> filterForSupportedType(Annotation[] annotations, Class<?> type) {
        List<AnnotationAndRoot> result = Lists.newArrayList();

        for (val ann : annotations) {
            if (supportType(ann, type)) {
                result.add(new AnnotationAndRoot(ann));
            }
        }

        return result;
    }

    private static Method getAsmDefaultAnnotations() {
        return AsmDefaultAnnotations.class.getMethods()[0];
    }

    public static boolean supportType(Annotation ann, Class<?> type) {
        val annClass = ann.annotationType();
        val asmConstraint = annClass.getAnnotation(AsmConstraint.class);
        for (val supportedClass : asmConstraint.supportedClasses()) {
            if (supportedClass.isAssignableFrom(type)) return true;
        }

        if (findMsaSupportType(asmConstraint, type) != null) return true;

        return asmTypeValidateGeneratorSupport(asmConstraint, type);
    }

    private static boolean asmTypeValidateGeneratorSupport(AsmConstraint asmConstraint, Class<?> type) {
        ObjenesisStd objStd = new ObjenesisStd();
        for (val asmValidateBy : asmConstraint.asmValidateBy()) {
            val clazz = AsmTypeValidateGenerator.class;
            if (!clazz.isAssignableFrom(asmValidateBy)) continue;

            val generator = (AsmTypeValidateGenerator) objStd.newInstance(asmValidateBy);
            if (generator.supportClass(type)) return true;
        }

        return false;
    }

    public static Class<? extends MsaValidator> findMsaSupportType(AsmConstraint asmConstraint, Class<?> type) {
        Class<?> wrap = Primitives.wrap(type);
        for (val msa : asmConstraint.validateBy()) {
            val arg = findSuperActualTypeArg(msa, MsaValidator.class, 1);
            if (arg == null) continue;
            if (arg.isAssignableFrom(wrap)) return msa;
        }

        return null;
    }

    public static Class<?> findSuperActualTypeArg(Class<?> subClass, Class<?> superClass, int argIndex) {
        for (val type : subClass.getGenericInterfaces()) {
            val argType = findSuperActualTypeArg(type, superClass, argIndex);
            if (argType != null) return argType;
        }

        Class<?> parentClass = subClass.getSuperclass();
        if (parentClass == Object.class) return null;

        return findSuperActualTypeArg(parentClass, superClass, argIndex);
    }

    private static Class<?> findSuperActualTypeArg(Type type, Class<?> superClass, int argIndex) {
        if (!(type instanceof ParameterizedType)) return null;

        val pType = (ParameterizedType) type;
        if (pType.getRawType() == superClass) {
            val argType = pType.getActualTypeArguments()[argIndex];
            if (argType instanceof Class) {
                return (Class<?>) argType;
            } else if (argType instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) argType).getRawType();
            }
        }

        return null;
    }

    private static void tryAddAsmMaxSize(List<AnnotationAndRoot> asmConstraintsAnns, Method defaultMethod, Class<?> type) {
        for (val ar : asmConstraintsAnns) {
            if (Arrays.anyOf(ar.ann().annotationType(), AsmMaxSize.class, AsmSize.class)) return;
        }

        val asmMaxSize = defaultMethod.getAnnotation(AsmMaxSize.class);
        if (supportType(asmMaxSize, type))
            asmConstraintsAnns.add(0, new AnnotationAndRoot(asmMaxSize));
    }

    private static void tryAddAsmNotBlank(List<AnnotationAndRoot> asmConstraintsAnns, Method defaultMethod, Class<?> type) {
        if (type.isPrimitive()) return;

        for (val ar : asmConstraintsAnns) {
            if (Arrays.anyOf(ar.ann().annotationType(),
                    AsmNotBlank.class, AsmBlankable.class,
                    AsmMinSize.class, AsmSize.class))
                return;
        }

        val asmNotBlank = defaultMethod.getAnnotation(AsmNotBlank.class);
        if (supportType(asmNotBlank, type)) {
            asmConstraintsAnns.add(0, new AnnotationAndRoot(asmNotBlank));
        }
    }


    public static void searchConstraints(List<AnnotationAndRoot> asmConstraintAnns, Annotation[] as) {
        searchConstraints(asmConstraintAnns, as, null);
    }

    public static void searchConstraints(List<AnnotationAndRoot> asmConstraintAnns, Annotation[] as, Annotation ra) {
        for (val a : as) {
            if (!a.annotationType().isAnnotationPresent(AsmConstraint.class)) continue;

            val subAnns = a.annotationType().getAnnotations();
            val rootAnn = ra == null ? a : ra;
            searchConstraints(asmConstraintAnns, subAnns, rootAnn);
            asmConstraintAnns.add(new AnnotationAndRoot(a, rootAnn));
        }
    }

    public static MethodVisitor startFieldValidatorMethod(ClassWriter cw, String fieldName, Class beanClass) {
        val mv = cw.visitMethod(ACC_PRIVATE,
                VALIDATE + StringUtils.capitalize(fieldName),
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

    public static boolean isAnnotationPresent(Annotation[] as, Class<?> ac) {
        for (val a : as) {
            if (ac.isInstance(a)) return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T findAnn(Annotation[] as, Class<T> ac) {
        for (val a : as) {
            if (ac.isInstance(a)) return (T) a;
        }

        return null;
    }

    public static void visitGetter(MethodVisitor mv, Field f) {
        mv.visitVarInsn(ALOAD, 1);
        val getterName = "get" + capitalize(f.getName());
        val declaringClass = f.getDeclaringClass();
        try {
            declaringClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            throw new AsmValidateBadUsageException("there is no getter method for field " + f.getName());
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, p(declaringClass), getterName, sig(f.getType()), false);
    }

    public static void addIsNullLocal(LocalIndices localIndices, MethodVisitor mv) {
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

    public static void createBridge(ClassWriter cw, Class beanClass, String implName) {
        val mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, MethodGeneratorUtils.VALIDATE,
                sig(AsmValidateResult.class, Object.class), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, p(beanClass));
        mv.visitMethodInsn(INVOKEVIRTUAL, p(implName), MethodGeneratorUtils.VALIDATE,
                sig(AsmValidateResult.class, beanClass), false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    public static void visitValidateFieldMethod(MethodVisitor mv, String implName, String fieldName, Class fieldClass) {
        mv.visitVarInsn(ALOAD, 0); // this
        mv.visitVarInsn(ALOAD, 1); // field value
        mv.visitVarInsn(ALOAD, 2); // AsmValidateResult
        mv.visitMethodInsn(INVOKESPECIAL, p(implName),
                MethodGeneratorUtils.VALIDATE + capitalize(fieldName),
                sig(void.class, fieldClass, AsmValidateResult.class), false);
    }

    public static boolean hasBlankable(List<AnnotationAndRoot> annotations) {
        for (val ar : annotations) {
            if (ar.ann().annotationType() == AsmBlankable.class) return true;
        }

        return false;
    }
}
