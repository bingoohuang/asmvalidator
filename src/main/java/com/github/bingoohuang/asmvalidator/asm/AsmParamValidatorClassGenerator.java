package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.github.bingoohuang.asmvalidator.annotations.AsmCreateClassFile4Debug;
import com.google.common.io.Files;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public class AsmParamValidatorClassGenerator {
    private final Method targetMethod;
    private final int targetParameterIndex;
    private final String implName;
    private final ClassWriter classWriter;

    public AsmParamValidatorClassGenerator(
            Method targetMethod, int targetParameterIndex) {
        this.targetMethod = targetMethod;
        this.targetParameterIndex = targetParameterIndex;
        String validatorSignature = AsmParamsValidatorFactory
                .createValidatorSignature(targetMethod);

        this.implName = validatorSignature + "$"
                + targetParameterIndex + "AsmValidator$BINGOOASM$Impl";
        this.classWriter = createClassWriter();
    }

    public Class<?> generate() {
        byte[] bytes = createImplClassBytes();

        createClassFileForDiagnose(bytes);

        return defineClass(bytes);
    }

    private void createClassFileForDiagnose(byte[] bytes) {
        if (targetMethod.isAnnotationPresent(AsmCreateClassFile4Debug.class))
            writeClassFile4Diagnose(bytes, implName + ".class");
    }

    private void writeClassFile4Diagnose(byte[] bytes, String fileName) {
        try {
            Files.write(bytes, new File(fileName));
        } catch (IOException e) {
            // ignore
        }
    }

    private Class<?> defineClass(byte[] bytes) {
        ClassLoader parentClassLoader = getClass().getClassLoader();
        AsmValidatorClassLoader classLoader =
                new AsmValidatorClassLoader(parentClassLoader);
        return classLoader.defineClass(implName, bytes);
    }

    private byte[] createImplClassBytes() {
        constructor();

        new AsmParamValidatorMethodGenerator(implName,
                targetMethod, targetParameterIndex, classWriter)
                .generate();

        return createBytes();
    }

    private byte[] createBytes() {
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private ClassWriter createClassWriter() {
        int flags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        ClassWriter cw = new ClassWriter(flags);
        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, p(implName),
                null, p(Object.class), new String[]{p(AsmValidator.class)});

        return cw;
    }

    private void constructor() {
        MethodVisitor mv;
        mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, p(Object.class),
                "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

}
