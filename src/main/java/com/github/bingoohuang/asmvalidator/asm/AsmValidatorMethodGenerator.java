package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

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
        extends AsmValidatorMethodGeneratable {

    private final Class<?> beanClass;

    public AsmValidatorMethodGenerator(
            Class<?> beanClass, ClassWriter classWriter, String implName) {
        super(classWriter, implName);

        this.beanClass = beanClass;
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

        if (anns.size() > 0) validateByAnnotations(
                localIndex, mv, field,
                field.getName(), field.getType(),
                anns, field.getAnnotations());

        if (isAsmValid(field)) asmValidate(mv, beanClass);

        if (isCollectionAndItemAsmValid(field.getType(), field.getGenericType()))
            collectionItemsValid(mv, field);

    }

    private void collectionItemsValid(MethodVisitor mv, Field field) {
        visitGetter(mv, field);
        mv.visitVarInsn(ALOAD, 2);

        mv.visitMethodInsn(INVOKESTATIC, p(AsmValidatorFactory.class),
                "validateAll",
                sig(void.class, Collection.class, AsmValidateResult.class),
                false);
    }


    /**
     * 为待校验的值（原值及转换后的字符串值）创建字节码局部变量。
     *
     * @param localIndices 局部变量索引
     * @param mv           方法访问器
     * @param field        字段
     */
    protected void createValueLocal(
            LocalIndices localIndices, MethodVisitor mv, Field field) {
        visitGetter(mv, field);
        localIndices.incrementAndSetOriginalLocalIndex();

        Class<?> fieldType = field.getType();
        mv.visitVarInsn(Asms.storeOpCode(fieldType), localIndices.getLocalIndex());
        mv.visitVarInsn(Asms.loadOpCode(fieldType), localIndices.getLocalIndex());
        AsmValidators.processWideLocal(fieldType, localIndices);

        if (fieldType == String.class) return;

        if (fieldType.isPrimitive()) localIndices.setOriginalPrimitive(true);

        if (fieldType.isPrimitive()) {
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

    private void createValidatorMainMethod() {
        MethodVisitor mv = startMainMethod(beanClass);

        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AsmIgnore.class)) continue;

            visitValidateFieldMethod(mv, implName, field.getName(), beanClass);
        }

        endMainMethod(mv);
    }

}
