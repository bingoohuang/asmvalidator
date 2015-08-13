package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.*;
import com.github.bingoohuang.asmvalidator.utils.AsmDefaultAnnotations;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidationGenerator;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmValidatorMethodGenerator {
    private final ClassWriter cw;
    private final String implName;
    private final Class<?> beanClass;
    private ObjenesisStd objenesisStd = new ObjenesisStd();

    public AsmValidatorMethodGenerator(
            Class<?> beanClass, ClassWriter classWriter, String implName) {
        this.beanClass = beanClass;
        this.cw = classWriter;
        this.implName = implName;
    }

    public void generate() {
        createValidatorMethod();
        createBridge();
    }

    private void createValidatorMethod() {
        createValidatorMainMethod();
        createValidatorFieldMethods();
    }

    private void createValidatorFieldMethods() {
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AsmIgnore.class)) continue;

            bodyFieldValidator(field);
        }
    }

    private void bodyFieldValidator(Field field) {
        MethodVisitor mv = startFieldValidatorMethod(field);

        // 0: this, 1:bean, 2: AsmValidateResult
        AtomicInteger localIndex = new AtomicInteger(2);
        List<Annotation> annotations = createAnnotationsForField(field);

        LocalIndices localIndices = new LocalIndices(localIndex);
        String defaultMessage = "";
        if (annotations.size() > 0) {
            createFieldValueLocal(localIndices, mv, field);
            Annotation lastAnn = annotations.get(annotations.size() - 1);
            defaultMessage = lastAnn.annotationType()
                    .getAnnotation(AsmConstraint.class).message();
            defaultMessage = AsmValidators.createMessage(lastAnn, defaultMessage);
        }

        for (Annotation fieldAnnotation : annotations) {
            Class<?> annType = fieldAnnotation.annotationType();
            AsmConstraint constraint = annType.getAnnotation(AsmConstraint.class);

            Class<? extends AsmValidationGenerator> validateByClz;
            validateByClz = constraint.validateBy();
            if (validateByClz == AsmNoopValidationGenerator.class) continue;

            AsmValidationGenerator validateBy = objenesisStd.newInstance(validateByClz);
            validateBy.generateAsm(mv, field.getName(), field.getType(),
                    fieldAnnotation, localIndices,
                    constraint, defaultMessage);
        }

        endFieldValidateMethod(mv);
    }


    private MethodVisitor startFieldValidatorMethod(Field field) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE,
                "validate" + StringUtils.capitalize(field.getName()),
                sig(void.class, beanClass, AsmValidateResult.class),
                null, null);
        mv.visitCode();
        return mv;
    }

    private void endFieldValidateMethod(MethodVisitor mv) {
        mv.visitInsn(RETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private List<Annotation> createAnnotationsForField(Field field) {
        List<Annotation> asmConstraintsAnns = Lists.newArrayList();
        searchAnnotations(asmConstraintsAnns, field);

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
            List<Annotation> asmConstraints, Field field) {
        Annotation[] annotations = field.getAnnotations();
        searchConstraints(asmConstraints, annotations);
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

    private void createFieldValueLocal(
            LocalIndices localIndices, MethodVisitor mv, Field field) {
        mv.visitVarInsn(ALOAD, 1);

        String fieldName = field.getName();
        String getterName = "get" + StringUtils.capitalize(fieldName);
        mv.visitMethodInsn(INVOKEVIRTUAL, p(field.getDeclaringClass()),
                getterName, sig(field.getType()), false);

        localIndices.incrementAndSetOriginalLocalIndex();

        if (field.getType().isPrimitive()) {
            localIndices.setOriginalPrimitive(true);
            if (field.getType() == int.class) {
                mv.visitVarInsn(ISTORE, localIndices.getLocalIndex());
                mv.visitVarInsn(ILOAD, localIndices.getLocalIndex());

                mv.visitMethodInsn(INVOKESTATIC, p(String.class),
                        "valueOf", sig(String.class, int.class), false);

                localIndices.incrementAndSetStringLocalIndex();

                mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
                mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            }
        } else {
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
                sig(AsmValidateResult.class, beanClass), null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, p(AsmValidateResult.class));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(AsmValidateResult.class),
                "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 2);

        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AsmIgnore.class)) continue;

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, p(implName),
                    "validate" + StringUtils.capitalize(field.getName()),
                    sig(void.class, beanClass, AsmValidateResult.class),
                    false);
        }

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private void createBridge() {
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
}
