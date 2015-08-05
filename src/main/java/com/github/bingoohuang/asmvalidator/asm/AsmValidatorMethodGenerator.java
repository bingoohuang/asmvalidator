package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.*;
import com.github.bingoohuang.asmvalidator.utils.AsmDefaultAnnotations;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

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

    public AsmValidatorMethodGenerator(Class<?> beanClass, ClassWriter classWriter, String implName) {
        this.beanClass = beanClass;
        this.cw = classWriter;
        this.implName = implName;
    }

    public void generate() {
        createValidatorMethod();
        createBridge();
    }

    private void createValidatorMethod() {
        MethodVisitor mv = startValidatorMethod();
        bodyValidatorMethod(mv);
        endValiateMethod(mv);
    }

    private void bodyValidatorMethod(MethodVisitor mv) {
        AtomicInteger localIndex = new AtomicInteger(2); // 0: this, 1:bean, 2: AsmValidateResult
        ObjenesisStd objenesisStd = new ObjenesisStd();

        for (Field field : beanClass.getDeclaredFields()) {
            Annotation[] annotations = field.getDeclaredAnnotations();

            // use default not empty and max size validator
            Method asmDefaultMethod = getAsmDefaultAnnotations();
            if (annotations.length == 0) annotations = asmDefaultMethod.getAnnotations();
            else {
                List<Annotation> defaultAnnotations = Lists.newArrayList();
                if (!field.isAnnotationPresent(AsmBlankable.class) && !field.isAnnotationPresent(AsmMinSize.class))
                    defaultAnnotations.add(asmDefaultMethod.getAnnotation(AsmNotBlank.class));
                if (!field.isAnnotationPresent(AsmMaxSize.class))
                    defaultAnnotations.add(asmDefaultMethod.getAnnotation(AsmMaxSize.class));

                defaultAnnotations.addAll(Arrays.asList(annotations));
                annotations = defaultAnnotations.toArray(new Annotation[0]);
            }

            if (annotations.length > 0) createFieldValueLocal(localIndex, mv, field);

            for (Annotation fieldAnnotation : annotations) {
                Class<?> annotationType = fieldAnnotation.annotationType();
                if (!annotationType.isAnnotationPresent(AsmConstraint.class)) continue;

                AsmConstraint asmConstraint = annotationType.getAnnotation(AsmConstraint.class);
                Class<? extends AsmValidationGenerator> validateByClass = asmConstraint.validateBy();
                AsmValidationGenerator validateBy = objenesisStd.newInstance(validateByClass);
                validateBy.generateAsm(mv, field, fieldAnnotation, localIndex);
            }
        }
    }

    private void createFieldValueLocal(AtomicInteger localIndex, MethodVisitor mv, Field field) {
        mv.visitVarInsn(ALOAD, 1);

        String fieldName = field.getName();
        String getterName = "get" + StringUtils.capitalize(fieldName);
        mv.visitMethodInsn(INVOKEVIRTUAL, p(field.getDeclaringClass()), getterName, sig(String.class), false);

        localIndex.incrementAndGet();
        mv.visitVarInsn(ASTORE, localIndex.get());
        mv.visitVarInsn(ALOAD, localIndex.get());
    }

    private Method getAsmDefaultAnnotations() {
        return AsmDefaultAnnotations.class.getMethods()[0];
    }

    private void endValiateMethod(MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private MethodVisitor startValidatorMethod() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "validate",
                sig(AsmValidateResult.class, beanClass), null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, p(AsmValidateResult.class));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(AsmValidateResult.class), "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 2);
        return mv;
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
