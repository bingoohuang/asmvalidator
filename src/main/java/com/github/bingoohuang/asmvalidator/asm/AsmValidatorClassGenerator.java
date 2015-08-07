package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.google.common.io.Files;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public class AsmValidatorClassGenerator {
    private final Class<?> beanClass;
    private final String implName;
    private final ClassWriter classWriter;

    public AsmValidatorClassGenerator(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.implName = beanClass.getName() + "AsmValidator$BINGOOASM$Impl";
        this.classWriter = createClassWriter();
    }

    public Class<?> generate() {
        byte[] bytes = createImplClassBytes();

        if (beanClass.isAnnotationPresent(CreateClassFile4Debug.class))
            createClassFileForDiagnose(bytes);

        return defineClass(bytes);
    }

    private void createClassFileForDiagnose(byte[] bytes) {
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
        ClassLoader parentClassLoader = beanClass.getClassLoader();
        AsmValidatorClassLoader classLoader =
                new AsmValidatorClassLoader(parentClassLoader);
        return classLoader.defineClass(implName, bytes);
    }

    private byte[] createImplClassBytes() {
        constructor();

        new AsmValidatorMethodGenerator(beanClass, classWriter, implName)
                .generate();

        return createBytes();
    }

    private byte[] createBytes() {
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private ClassWriter createClassWriter() {
        ClassWriter cw = new ClassWriter(0);
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
