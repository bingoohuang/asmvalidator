package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.*;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.validation.AsmCustomValidateGenerator;
import lombok.val;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.tryGetAsmMessage;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.addIsNullLocal;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.findMsaSupportType;
import static org.objectweb.asm.Opcodes.*;

public abstract class AsmValidatorMethodGeneratable {
    protected final ClassWriter cw;
    protected final String implName;

    protected ObjenesisStd objenesisStd = new ObjenesisStd();

    public AsmValidatorMethodGeneratable(ClassWriter classWriter, String implName) {
        this.cw = classWriter;
        this.implName = implName;
    }

    public abstract void generate();

    protected void validateByAnnotations(
            AtomicInteger localIndex, MethodVisitor mv,
            Field field,
            String fieldName, Class<?> fieldType,
            List<AnnotationAndRoot> anns,
            Annotation[] targetAnns
    ) {
        val localIndices = new LocalIndices(localIndex);
        createValueLocal(localIndices, mv, field);
        addIsNullLocal(localIndices, mv);

        String defaultMessage = tryGetAsmMessage(targetAnns);

        AsmConstraint constraint;
        for (val annAndRoot : anns) {
            val annType = annAndRoot.ann().annotationType();
            constraint = annType.getAnnotation(AsmConstraint.class);

            for (val validateByClz : constraint.asmValidateBy()) {
                generateAsmValidateCode(mv, localIndices,
                        defaultMessage, fieldType, fieldName,
                        validateByClz, annAndRoot);
            }

            val msaSupportType = findMsaSupportType(constraint, fieldType);
            if (msaSupportType != null) {
                generateAsmValidateCode(mv, localIndices,
                        defaultMessage, fieldType, fieldName,
                        AsmCustomValidateGenerator.class, annAndRoot);
            }
        }

    }

    protected abstract void createValueLocal(
            LocalIndices localIndices, MethodVisitor mv, Field field);


    protected void asmValidate(MethodVisitor mv, Class<?> targetClass) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, p(AsmValidatorFactory.class),
                "validate",
                sig(void.class, targetClass, AsmValidateResult.class), false);
    }

    protected void generateAsmValidateCode(
            MethodVisitor mv, LocalIndices localIndices,
            String defaultMessage,
            Class<?> fieldType, String fieldName,
            Class<? extends AsmValidateGenerator> asmValidateByClz,
            AnnotationAndRoot annAndRoot
    ) {
        val asmValidateBy = objenesisStd.newInstance(asmValidateByClz);
        if (asmValidateBy instanceof AsmTypeValidateGenerator) {
            val tg = (AsmTypeValidateGenerator) asmValidateBy;
            if (!tg.supportClass(fieldType)) return;
        }

        asmValidateBy.generateAsm(mv, fieldName, fieldType,
                annAndRoot, localIndices, defaultMessage);
    }


    protected MethodVisitor startMainMethod(Class<?> targetClass) {
        val mv = cw.visitMethod(ACC_PUBLIC, "validate",
                sig(AsmValidateResult.class, targetClass), null, null);

        mv.visitCode();
        mv.visitTypeInsn(NEW, p(AsmValidateResult.class));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(AsmValidateResult.class),
                "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 2);

        return mv;
    }

    protected void endMainMethod(MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }
}
