package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.utils.Arrays;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.isCollectionAndItemAsmValid;
import static com.github.bingoohuang.asmvalidator.utils.Asms.*;
import static com.github.bingoohuang.asmvalidator.utils.MethodGeneratorUtils.*;
import static org.objectweb.asm.Opcodes.*;

@Slf4j
public class AsmValidatorMethodGenerator extends AsmValidatorMethodGeneratable {
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
        for (val f : beanClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(AsmIgnore.class)
                    || Modifier.isStatic(f.getModifiers())) continue;

            val mv = startFieldValidatorMethod(cw, f.getName(), beanClass);
            bodyFieldValidator(mv, f);
            endFieldValidateMethod(mv);
        }
    }

    private void bodyFieldValidator(MethodVisitor mv, Field field) {
        // 0: this, 1:bean, 2: AsmValidateResult
        val localIndex = new AtomicInteger(2);

        val anns = createValidateAnns(field.getAnnotations(), field.getType());

        val checkBlank = hasBlankable(anns);
        if (anns.size() > 0) {
            validateByAnnotations(localIndex, mv, field,
                    field.getName(), field.getType(), field.getGenericType(),
                    anns, field.getAnnotations(), checkBlank);
        }

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
        mv.visitVarInsn(storeOpCode(fieldType), localIndices.getLocalIndex());
        mv.visitVarInsn(loadOpCode(fieldType), localIndices.getLocalIndex());
        AsmValidators.processWideLocal(fieldType, localIndices);

        if (fieldType == String.class) return;
        if (fieldType.isPrimitive()) {
            localIndices.setOriginalPrimitive(true);

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
        val mv = startMainMethod(beanClass);

        for (val field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AsmIgnore.class)
                    || Modifier.isStatic(field.getModifiers())) continue;

            visitValidateFieldMethod(mv, implName, field.getName(), beanClass);
        }

        createCustomValidateMethods(mv);

        endMainMethod(mv);
    }


    private void createCustomValidateMethods(MethodVisitor mv) {
        for (val f : beanClass.getMethods()) {
            if (!f.isAnnotationPresent(AsmValid.class)) continue;
            if (!Arrays.anyOf(f.getReturnType(), void.class, Void.class)) {
                log.warn("{} is annotated by @AsmValid without void return is ignored!", f);
                continue;
            }

            int parameterCount = f.getParameterTypes().length;

            if (parameterCount == 0) {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, p(beanClass), f.getName(), sig(void.class), false);
            } else if (parameterCount == 1 && f.getParameterTypes()[0] == AsmValidateResult.class) {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2); // AsmValidateResult
                mv.visitMethodInsn(INVOKEVIRTUAL, p(beanClass), f.getName(), sig(void.class, AsmValidateResult.class), false);
            } else {
                log.warn("{} is annotated by @AsmValid is ignored because of unsupported parameter types!", f);
            }
        }
    }
}
