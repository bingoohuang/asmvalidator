package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.google.common.primitives.Primitives;
import lombok.val;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.isCollectionAndItemAsmValid;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.*;
import static org.objectweb.asm.Opcodes.*;

public class AsmParamValidatorMethodGenerator
        extends AsmValidatorMethodGeneratable {
    private final Annotation[] targetAnns;
    private final Class<?> targetType;

    private final String fieldName;
    private final Type genericType;
    private final Class<?> wrapTargetType;

    public AsmParamValidatorMethodGenerator(
            String implName, Method targetMethod, int targetParameterIndex,
            ClassWriter classWriter) {
        super(classWriter, implName);
        this.fieldName = "arg" + targetParameterIndex;

        Annotation[][] paramsAnns = targetMethod.getParameterAnnotations();
        this.targetAnns = paramsAnns[targetParameterIndex];
        this.targetType = targetMethod.getParameterTypes()[targetParameterIndex];
        this.wrapTargetType = Primitives.wrap(targetType);
        this.genericType = targetMethod.getGenericParameterTypes()[targetParameterIndex];
    }

    public void generate() {
        createValidatorMainMethod();
        val mv = startFieldValidatorMethod(cw, fieldName, Object.class);
        bodyParamValidator(mv);
        endFieldValidateMethod(mv);
    }

    private void bodyParamValidator(MethodVisitor mv) {
        // 0: this, 1:bean, 2: AsmValidateResult
        val localIndex = new AtomicInteger(2);

        val annotations = createValidateAnns(targetAnns, targetType);
        boolean checkBlank = hasBlankable(annotations);
        if (annotations.size() > 0) validateByAnnotations(
                localIndex, mv, null,
                fieldName, wrapTargetType,
                annotations, targetAnns, checkBlank);

        if (isAsmValid()) asmValidate(mv, Object.class);
        if (isCollectionAndItemAsmValid(targetType, genericType))
            collectionItemsValid(mv);
    }

    protected void createValueLocal(
            LocalIndices localIndices, MethodVisitor mv, Field field) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, p(wrapTargetType));

        if (targetType == String.class) {
            localIndices.incrementAndSetStringLocalIndex();
            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            return;
        }

        if (targetType.isPrimitive()) {
            int localIndex = localIndices.incrementLocalIndex();
            mv.visitVarInsn(ASTORE, localIndex);
            localIndices.setOriginalLocalIndex(localIndex);

            mv.visitVarInsn(ALOAD, localIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, p(wrapTargetType),
                    "toString", sig(String.class), false);

            localIndices.incrementAndSetStringLocalIndex();
            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            return;
        }
    }

    private void collectionItemsValid(MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, p(AsmValidatorFactory.class),
                "validateAll",
                sig(void.class, Collection.class, AsmValidateResult.class),
                false);
    }

    private boolean isAsmValid() {
        return targetType.isAnnotationPresent(AsmValid.class);
    }

    private void createValidatorMainMethod() {
        val mv = startMainMethod(Object.class);

        if (!isAnnotationPresent(targetAnns, AsmIgnore.class)) {
            visitValidateFieldMethod(mv, implName, fieldName, Object.class);
        }

        endMainMethod(mv);
    }

}
