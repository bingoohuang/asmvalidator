package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.validation.AsmCustomValidateGenerator;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidateGenerator;
import com.github.bingoohuang.asmvalidator.validation.MsaNoopValidator;
import com.google.common.primitives.Primitives;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.isListAndItemAsmValid;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.*;
import static org.objectweb.asm.Opcodes.*;

public class AsmParamValidatorMethodGenerator
        implements AsmValidatorMethodGeneratable {
    private final ClassWriter cw;
    private final Annotation[] targetAnns;
    private final Class<?> targetType;
    private final String implName;
    private final String fieldName;
    private final Type genericType;
    private ObjenesisStd objenesisStd = new ObjenesisStd();

    public AsmParamValidatorMethodGenerator(
            String implName, Method targetMethod, int targetParameterIndex,
            ClassWriter classWriter) {
        this.implName = implName;
        this.cw = classWriter;
        this.fieldName = "arg" + targetParameterIndex;

        Annotation[][] paramsAnns = targetMethod.getParameterAnnotations();
        this.targetAnns = paramsAnns[targetParameterIndex];
        this.targetType = targetMethod.getParameterTypes()[targetParameterIndex];
        this.genericType = targetMethod.getGenericParameterTypes()[targetParameterIndex];
    }

    public void generate() {
        createValidatorMainMethod();
        MethodVisitor mv = startFieldValidatorMethod(cw, fieldName, Object.class);
        bodyParamValidator(mv);
        endFieldValidateMethod(mv);
    }

    private void bodyParamValidator(MethodVisitor mv) {
        List<AnnotationAndRoot> annotations = createValidateAnns(
                targetAnns, targetType);
        if (annotations.size() == 0) return;

        if (!isParameterValidateSupported()) return;
        if (isAsmValidAndCall(mv)) return;

        // 0: this, 1:bean, 2: AsmValidateResult
        AtomicInteger localIndex = new AtomicInteger(2);
        LocalIndices localIndices = new LocalIndices(localIndex);
        createValueLocal(localIndices, mv);
        addIsStringNullLocal(localIndices, mv);

        String defaultMessage = AsmValidators.tryGetAsmMessage(targetAnns);

        AsmConstraint constraint;
        Class<? extends AsmValidateGenerator> asmValidateByClz;

        for (AnnotationAndRoot annAndRoot : annotations) {
            Class<?> annType = annAndRoot.ann().annotationType();
            constraint = annType.getAnnotation(AsmConstraint.class);

            asmValidateByClz = constraint.asmValidateBy();
            if (asmValidateByClz != AsmNoopValidateGenerator.class) {
                generateAsmValidateCode(mv, localIndices,
                        defaultMessage, constraint, asmValidateByClz, annAndRoot);
            }

            if (constraint.validateBy() != MsaNoopValidator.class) {
                generateAsmValidateCode(mv, localIndices,
                        defaultMessage, constraint,
                        AsmCustomValidateGenerator.class, annAndRoot);
            }
        }

    }

    private void generateAsmValidateCode(
            MethodVisitor mv, LocalIndices localIndices,
            String defaultMessage,
            AsmConstraint constraint,
            Class<? extends AsmValidateGenerator> asmValidateByClz,
            AnnotationAndRoot annAndRoot) {
        AsmValidateGenerator asmValidateBy;
        asmValidateBy = objenesisStd.newInstance(asmValidateByClz);
        asmValidateBy.generateAsm(mv, fieldName, targetType,
                annAndRoot, localIndices,
                defaultMessage);
    }

    boolean isParameterValidateSupported() {
        if (targetType == String.class) return true;
        if (targetType == int.class) return true;
        if (targetType == long.class) return true;
        if (isListAndItemAsmValid(targetType, genericType)) return true;
        if (isAsmValid()) return true;

        return false;
    }

    void createValueLocal(LocalIndices localIndices, MethodVisitor mv) {
        if (targetType == String.class) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, p(String.class));
            localIndices.incrementAndSetStringLocalIndex();
            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            return;
        }

        if (targetType == int.class || targetType == long.class) {
            mv.visitVarInsn(ALOAD, 1);
            Class<?> wrapClass = Primitives.wrap(targetType);
            mv.visitTypeInsn(CHECKCAST, p(wrapClass));
            AsmValidators.processWideLocal(targetType, localIndices);
            int localIndex = localIndices.incrementLocalIndex();
            mv.visitVarInsn(ASTORE, localIndex);
            localIndices.setOriginalLocalIndex(localIndex);

            mv.visitVarInsn(ALOAD, localIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, p(wrapClass),
                    "toString", sig(String.class), false);

            localIndices.incrementAndSetStringLocalIndex();
            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());

            return;
        }
    }

    private boolean isAsmValidAndCall(MethodVisitor mv) {
        if (isAsmValid()) {
            asmValidate(mv);
            return true;
        }

        if (targetType == List.class) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, p(List.class));
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);

            Class itemClass = AsmValidators.getListItemClass(genericType);
            AsmValidators.validateListItems(mv, itemClass);

            return true;
        }

        return false;
    }

    private boolean isAsmValid() {
        return targetType.isAnnotationPresent(AsmValid.class);
    }

    private void asmValidate(MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, p(AsmValidatorFactory.class),
                "validate",
                sig(void.class, Object.class, AsmValidateResult.class), false);
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

        if (!isAnnotationPresent(targetAnns, AsmIgnore.class)) {
            visitValidateFieldMethod(mv, implName, fieldName, Object.class);
        }

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

}
