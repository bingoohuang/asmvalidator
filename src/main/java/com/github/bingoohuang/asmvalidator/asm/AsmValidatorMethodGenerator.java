package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmMessage;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils;
import com.github.bingoohuang.asmvalidator.validation.AsmNoopValidateGenerator;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.*;
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
        List<Annotation> anns = createValidateAnns(field.getAnnotations());
        if (anns.size() == 0) return;

        if (!isFieldValidateSupported(field)) return;
        if (isAsmValidAndCall(mv, field)) return;

        // 0: this, 1:bean, 2: AsmValidateResult
        AtomicInteger localIndex = new AtomicInteger(2);
        LocalIndices localIndices = new LocalIndices(localIndex);
        createFieldValueLocal(localIndices, mv, field);
        addIsStringNullLocal(localIndices, mv);

        AsmMessage asmMessage = findAnn(field.getAnnotations(), AsmMessage.class);
        String defaultMessage = asmMessage != null ? asmMessage.value() : "";

        AsmConstraint constraint;
        AsmValidateGenerator validateBy;
        Class<? extends AsmValidateGenerator> validateByClz;

        for (Annotation fieldAnnotation : anns) {
            Class<?> annType = fieldAnnotation.annotationType();
            constraint = annType.getAnnotation(AsmConstraint.class);

            validateByClz = constraint.validateBy();
            if (validateByClz == AsmNoopValidateGenerator.class) continue;

            validateBy = objenesisStd.newInstance(validateByClz);
            validateBy.generateAsm(mv, field.getName(), field.getType(),
                    fieldAnnotation, localIndices,
                    constraint, defaultMessage);
        }
    }

    /**
     * 字段是否支持校验
     *
     * @param field 字段
     * @return true支持 false不支持
     */
    private boolean isFieldValidateSupported(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == String.class) return true;
        if (fieldType == int.class) return true;

        return false;
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
        mv.visitVarInsn(ALOAD, 1);

        visitGetter(mv, field);

        localIndices.incrementAndSetOriginalLocalIndex();

        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            return;
        }

        if (fieldType.isPrimitive()) {
            localIndices.setOriginalPrimitive(true);
        }

        if (fieldType == int.class) {
            mv.visitVarInsn(ISTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ILOAD, localIndices.getLocalIndex());

            mv.visitMethodInsn(INVOKESTATIC, p(String.class),
                    "valueOf", sig(String.class, int.class), false);

            localIndices.incrementAndSetStringLocalIndex();

            mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            return;
        }

    }

    private boolean isAsmValidAndCall(MethodVisitor mv, Field field) {
        if (!isAsmValid(field)) return false;

        asmValidate(mv, beanClass);
        return true;
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
