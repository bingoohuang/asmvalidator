package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
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
                "validateAll", sig(void.class, Collection.class, AsmValidateResult.class), false);
    }


    /**
     * 为待校验的值（原值及转换后的字符串值）创建字节码局部变量。
     *
     * @param localIndices 局部变量索引
     * @param mv           方法访问器
     * @param f            字段
     */
    protected void createValueLocal(LocalIndices localIndices, MethodVisitor mv, Field f) {
        visitGetter(mv, f);
        localIndices.incrementAndSetOriginalLocalIndex();

        mv.visitVarInsn(storeOpCode(f.getType()), localIndices.getLocalIndex());
        mv.visitVarInsn(loadOpCode(f.getType()), localIndices.getLocalIndex());
        AsmValidators.processWideLocal(f.getType(), localIndices);

        if (f.getType() == String.class) return;
        if (f.getType().isPrimitive()) {
            localIndices.setOriginalPrimitive(true);

            mv.visitMethodInsn(INVOKESTATIC, p(String.class), "valueOf", sig(String.class, f.getType()), false);

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

        for (val f : beanClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(AsmIgnore.class)
                    || Modifier.isStatic(f.getModifiers())) continue;

            visitValidateFieldMethod(mv, implName, f.getName(), beanClass);
        }

        createCustomValidateMethods(mv);

        endMainMethod(mv);
    }


    private void createCustomValidateMethods(MethodVisitor mv) {
        for (val f : beanClass.getMethods()) {
            if (!f.isAnnotationPresent(AsmValid.class)) continue;
            if (f.getReturnType() != void.class) {
                log.warn("{} is annotated by @AsmValid without void return is ignored!", f);
                continue;
            }

            if (f.getParameterTypes().length == 0) {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, p(beanClass), f.getName(), sig(void.class), false);
            } else if (f.getParameterTypes().length == 1 && f.getParameterTypes()[0] == AsmValidateResult.class) {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2); // AsmValidateResult
                mv.visitMethodInsn(INVOKEVIRTUAL, p(beanClass), f.getName(), sig(void.class, AsmValidateResult.class), false);
            } else {
                log.warn("{} is annotated by @AsmValid is ignored because of unsupported parameter types!", f);
            }
        }
    }
}
