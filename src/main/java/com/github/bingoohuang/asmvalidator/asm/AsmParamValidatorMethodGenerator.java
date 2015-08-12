package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.*;
import com.github.bingoohuang.asmvalidator.utils.AsmDefaultAnnotations;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidationGenerator;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmParamValidatorMethodGenerator {
    private final Method targetMethod;
    private final int targetParameterIndex;
    private final ClassWriter cw;
    private final Annotation[] targetAnnotations;
    private final Class<?> targetParamType;
    private final String implName;
    private final String fieldName;
    private ObjenesisStd objenesisStd = new ObjenesisStd();

    public AsmParamValidatorMethodGenerator(
            String implName, Method targetMethod, int targetParameterIndex,
            ClassWriter classWriter) {
        this.implName = implName;
        this.targetMethod = targetMethod;
        this.targetParameterIndex = targetParameterIndex;
        this.cw = classWriter;
        this.fieldName = "arg" + targetParameterIndex;

        Annotation[][] paramsAnns = targetMethod.getParameterAnnotations();
        this.targetAnnotations = paramsAnns[targetParameterIndex];
        this.targetParamType = targetMethod.getParameterTypes()[targetParameterIndex];
    }

    public void generate() {
        createValidatorMainMethod();
        bodyFieldValidator();
    }

    private void bodyFieldValidator() {
        MethodVisitor mv = startFieldValidatorMethod();

        // 0: this, 1:bean, 2: AsmValidateResult
        AtomicInteger localIndex = new AtomicInteger(2);
        List<Annotation> annotations = createAnnotationsForParam();

        LocalIndices localIndices = new LocalIndices(localIndex);
        String defaultMessage = "";
        if (annotations.size() > 0) {
            createValueLocal(localIndices, mv);

            boolean hasAsmMessage = isAnnotationPresent(AsmMessage.class);
            if (hasAsmMessage) {
                AsmMessage asmMessage = findAnnotationPresent(AsmMessage.class);
                defaultMessage = asmMessage.value();
            }
        }

        for (Annotation fieldAnnotation : annotations) {
            Class<?> annType = fieldAnnotation.annotationType();
            AsmConstraint constraint = annType.getAnnotation(AsmConstraint.class);

            Class<? extends AsmValidationGenerator> validateByClz;
            validateByClz = constraint.validateBy();
            if (validateByClz == AsmNoopValidationGenerator.class) continue;

            AsmValidationGenerator validateBy = objenesisStd.newInstance(validateByClz);

            validateBy.generateAsm(mv, fieldName, targetParamType,
                    fieldAnnotation, localIndices,
                    constraint, defaultMessage);
        }


        endFieldValidateMethod(mv);
    }


    private MethodVisitor startFieldValidatorMethod() {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE,
                "validate" + StringUtils.capitalize(fieldName),
                sig(void.class, Object.class, AsmValidateResult.class),
                null, null);
        mv.visitCode();
        return mv;
    }

    private void endFieldValidateMethod(MethodVisitor mv) {
        mv.visitInsn(RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }


    private List<Annotation> createAnnotationsForParam() {
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

    private void tryAddAsmMaxSize(List<Annotation> asmConstraintsAnns, Method defaultMethod) {
        for (Annotation ann : asmConstraintsAnns) {
            if (ann.annotationType() == AsmMaxSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        asmConstraintsAnns.add(0, defaultMethod.getAnnotation(AsmMaxSize.class));
    }

    private void tryAddAsmNotBlank(List<Annotation> asmConstraintsAnns, Method defaultMethod) {
        for (Annotation ann : asmConstraintsAnns) {
            if (ann.annotationType() == AsmBlankable.class) return;
            if (ann.annotationType() == AsmMinSize.class) return;
            if (ann.annotationType() == AsmSize.class) return;
        }

        asmConstraintsAnns.add(0, defaultMethod.getAnnotation(AsmNotBlank.class));
    }

    private void searchAnnotations(
            List<Annotation> asmConstraints, Annotation annotation) {
        Annotation[] annotations = annotation.annotationType().getAnnotations();
        searchConstraints(asmConstraints, annotations);
    }

    private void searchConstraints(
            List<Annotation> asmConstraintAnns, Annotation[] annotations) {
        for (Annotation ann : annotations) {
            Class<?> annType = ann.annotationType();
            AsmConstraint asmConstraint = annType.getAnnotation(AsmConstraint.class);
            if (asmConstraint == null) continue;

            searchAnnotations(asmConstraintAnns, ann);
            asmConstraintAnns.add(ann);
        }
    }

    private void createValueLocal(LocalIndices localIndices, MethodVisitor mv) {
        if (targetParamType.isPrimitive()) {
            if (targetParamType == int.class) {
                mv.visitVarInsn(ILOAD, 1);

                mv.visitMethodInsn(INVOKESTATIC, p(String.class),
                        "valueOf", sig(String.class, int.class), false);

                localIndices.incrementAndSetStringLocalIndex();

                mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
                mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            }
        } else {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, p(String.class));
            localIndices.incrementAndSetStringLocalIndex();
            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
        }


        addIsStringNullLocal(localIndices, mv);
    }

    private void addIsStringNullLocal(
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

    private Method getAsmDefaultAnnotations() {
        return AsmDefaultAnnotations.class.getMethods()[0];
    }

    private void createValidatorMainMethod() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "validate",
                sig(AsmValidateResult.class, Object.class), null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, p(AsmValidateResult.class));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(AsmValidateResult.class),
                "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 2);

        if (!isAnnotationPresent(AsmIgnore.class)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, p(implName),
                    "validate" + StringUtils.capitalize(fieldName),
                    sig(void.class, Object.class, AsmValidateResult.class),
                    false);
        }

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private boolean isAnnotationPresent(Class<?> annotationType) {
        for (Annotation ann : targetAnnotations) {
            if (annotationType.isInstance(ann)) return true;
        }

        return false;
    }


    private <T> T findAnnotationPresent(Class<T> annotationType) {
        for (Annotation ann : targetAnnotations) {
            if (annotationType.isInstance(ann)) return (T) ann;
        }

        return null;
    }

}
