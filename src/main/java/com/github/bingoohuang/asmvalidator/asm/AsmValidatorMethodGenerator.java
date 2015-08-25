package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils;
import com.github.bingoohuang.asmvalidator.validation.AsmCustomValidateGenerator;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidateGenerator;
import com.github.bingoohuang.asmvalidator.validation.MsaNoopValidator;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.isCollectionAndItemAsmValid;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.*;
import static org.objectweb.asm.Opcodes.*;

public class AsmValidatorMethodGenerator
        implements AsmValidatorMethodGeneratable {
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
        createBridge(cw, beanClass, implName);
    }

    private void createValidatorMethod() {
        createValidatorMainMethod();
        createValidatorFieldMethods();
    }

    private void createValidatorFieldMethods() {
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AsmIgnore.class)) continue;

            MethodVisitor mv = startFieldValidatorMethod(cw, field.getName(), beanClass);
            bodyFieldValidator(mv, field);
            MethodGeneratorUtils.endFieldValidateMethod(mv);
        }
    }

    private void bodyFieldValidator(MethodVisitor mv, Field field) {
        // 0: this, 1:bean, 2: AsmValidateResult
        AtomicInteger localIndex = new AtomicInteger(2);

        List<AnnotationAndRoot> anns = createValidateAnns(
                field.getAnnotations(), field.getType());

        if (anns.size() > 0) validateByAnnotations(localIndex, mv, field, anns);

        if (isAsmValid(field)) asmValidate(mv, beanClass);

        if (isCollectionAndItemAsmValid(field.getType(), field.getGenericType())) {
            visitGetter(mv, field);
            mv.visitVarInsn(ALOAD, 2);

            mv.visitMethodInsn(INVOKESTATIC, p(AsmValidatorFactory.class),
                    "validateAll",
                    sig(void.class, Collection.class, AsmValidateResult.class),
                    false);
        }

    }

    private void validateByAnnotations(
            AtomicInteger localIndex, MethodVisitor mv,
            Field field, List<AnnotationAndRoot> anns) {

        LocalIndices localIndices = new LocalIndices(localIndex);
        createFieldValueLocal(localIndices, mv, field);
        addIsNullLocal(localIndices, mv);

        String defaultMessage = AsmValidators.tryGetAsmMessage(field.getAnnotations());

        AsmConstraint constraint;
        Class<? extends AsmValidateGenerator> validateByClz;

        for (AnnotationAndRoot annAndRoot : anns) {
            Class<?> annType = annAndRoot.ann().annotationType();
            constraint = annType.getAnnotation(AsmConstraint.class);

            validateByClz = constraint.asmValidateBy();
            if (validateByClz != AsmNoopValidateGenerator.class) {
                generateAsmValidateCode(mv, field, localIndices,
                        defaultMessage,
                        validateByClz, annAndRoot);
            }

            if (constraint.validateBy() != MsaNoopValidator.class) {
                generateAsmValidateCode(mv, field, localIndices,
                        defaultMessage,
                        AsmCustomValidateGenerator.class, annAndRoot);
            }
        }

    }

    private void generateAsmValidateCode(
            MethodVisitor mv, Field field, LocalIndices localIndices,
            String defaultMessage,
            Class<? extends AsmValidateGenerator> validateByClz,
            AnnotationAndRoot annAndRoot) {
        AsmValidateGenerator validateBy;
        validateBy = objenesisStd.newInstance(validateByClz);
        validateBy.generateAsm(mv, field.getName(), field.getType(),
                annAndRoot, localIndices,
                defaultMessage);
    }

    /**
     * 为待校验的值（原值及转换后的字符串值）创建字节码局部变量。
     *
     * @param localIndices 局部变量索引
     * @param mv           方法访问器
     * @param field        字段
     */
    private void createFieldValueLocal(
            LocalIndices localIndices, MethodVisitor mv, Field field) {
        visitGetter(mv, field);
        localIndices.incrementAndSetOriginalLocalIndex();

        Class<?> fieldType = field.getType();
        mv.visitVarInsn(Asms.storeOpCode(fieldType), localIndices.getLocalIndex());
        mv.visitVarInsn(Asms.loadOpCode(fieldType), localIndices.getLocalIndex());
        AsmValidators.processWideLocal(fieldType, localIndices);

        if (fieldType == String.class) return;

        if (fieldType.isPrimitive()) localIndices.setOriginalPrimitive(true);

        if (fieldType == int.class || fieldType == long.class) {
            mv.visitMethodInsn(INVOKESTATIC, p(String.class),
                    "valueOf", sig(String.class, fieldType), false);

            localIndices.incrementAndSetStringLocalIndex();

            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            return;
        }

    }


    private boolean isAsmValid(Field field) {
        return field.isAnnotationPresent(AsmValid.class);
    }

    private void asmValidate(MethodVisitor mv, Class fieldClass) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, p(AsmValidatorFactory.class),
                "validate",
                sig(void.class, fieldClass, AsmValidateResult.class), false);
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

            visitValidateFieldMethod(mv, implName, field.getName(), beanClass);
        }

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }
}
