package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmMessage;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.*;
import static org.objectweb.asm.Opcodes.*;

public class AsmParamValidatorMethodGenerator {
    private final ClassWriter cw;
    private final Annotation[] targetAnns;
    private final Class<?> targetParamType;
    private final String implName;
    private final String fieldName;
    private ObjenesisStd objenesisStd = new ObjenesisStd();

    public AsmParamValidatorMethodGenerator(
            String implName, Method targetMethod, int targetParameterIndex,
            ClassWriter classWriter) {
        this.implName = implName;
        this.cw = classWriter;
        this.fieldName = "arg" + targetParameterIndex;

        Annotation[][] paramsAnns = targetMethod.getParameterAnnotations();
        this.targetAnns = paramsAnns[targetParameterIndex];
        this.targetParamType = targetMethod.getParameterTypes()[targetParameterIndex];
    }

    public void generate() {
        createValidatorMainMethod();
        MethodVisitor mv = startFieldValidatorMethod(cw, fieldName, Object.class);
        bodyParamValidator(mv);
        endFieldValidateMethod(mv);
    }

    private void bodyParamValidator(MethodVisitor mv) {
        List<Annotation> annotations = createValidateAnns(targetAnns, targetParamType);
        if (annotations.size() == 0) return;

        if (!isParameterValidateSupported()) return;
        if (isAsmValidAndCall(mv)) return;

        // 0: this, 1:bean, 2: AsmValidateResult
        AtomicInteger localIndex = new AtomicInteger(2);
        LocalIndices localIndices = new LocalIndices(localIndex);
        createValueLocal(localIndices, mv);
        addIsStringNullLocal(localIndices, mv);

        AsmMessage asmMessage = findAnn(targetAnns, AsmMessage.class);
        String defaultMessage = asmMessage != null ? asmMessage.value() : "";

        AsmConstraint constraint;
        Class<? extends AsmValidateGenerator> asmValidateByClz;

        for (Annotation fieldAnn : annotations) {
            Class<?> annType = fieldAnn.annotationType();
            constraint = annType.getAnnotation(AsmConstraint.class);

            asmValidateByClz = constraint.asmValidateBy();
            if (asmValidateByClz != AsmNoopValidateGenerator.class) {
                generateAsmValidateCode(mv, localIndices,
                        defaultMessage, constraint, asmValidateByClz, fieldAnn);
            }

            if (constraint.validateBy() != MsaNoopValidator.class) {
                generateAsmValidateCode(mv, localIndices,
                        defaultMessage, constraint,
                        AsmCustomValidateGenerator.class, fieldAnn);
            }
        }

    }

    private void generateAsmValidateCode(
            MethodVisitor mv, LocalIndices localIndices,
            String defaultMessage, AsmConstraint constraint,
            Class<? extends AsmValidateGenerator> asmValidateByClz,
            Annotation fieldAnn) {
        AsmValidateGenerator asmValidateBy;
        asmValidateBy = objenesisStd.newInstance(asmValidateByClz);
        asmValidateBy.generateAsm(mv, fieldName, targetParamType,
                fieldAnn, localIndices,
                constraint, defaultMessage);
    }

    boolean isParameterValidateSupported() {
        if (targetParamType == String.class) return true;
        if (targetParamType == int.class) return true;
        if (targetParamType == long.class) return true;
        if (isAsmValid()) return true;

        return false;
    }

    void createValueLocal(LocalIndices localIndices, MethodVisitor mv) {
        if (targetParamType == String.class) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, p(String.class));
            localIndices.incrementAndSetStringLocalIndex();
            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            return;
        }

        if (targetParamType == int.class || targetParamType == long.class) {
            mv.visitVarInsn(ALOAD, 1);
            Class<?> wrapClass = Primitives.wrap(targetParamType);
            mv.visitTypeInsn(CHECKCAST, p(wrapClass));
            AsmValidators.processWideLocal(targetParamType, localIndices);
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
        if (!isAsmValid()) return false;

        asmValidate(mv);
        return true;
    }

    private boolean isAsmValid() {
        return targetParamType.isAnnotationPresent(AsmValid.class);
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
