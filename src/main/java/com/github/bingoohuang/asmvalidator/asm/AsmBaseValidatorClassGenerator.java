package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.google.common.io.Files;
import lombok.val;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static org.objectweb.asm.Opcodes.*;

public abstract class AsmBaseValidatorClassGenerator {
    protected final String implName;
    protected final ClassWriter classWriter;

    public AsmBaseValidatorClassGenerator(
            String implName) {
        this.implName = implName;
        this.classWriter = createClassWriter();
    }

    public Class<?> generate() {
        byte[] bytes = createImplClassBytes();

        createClassFileForDiagnose(bytes);

        return defineClass(bytes);
    }

    private byte[] createImplClassBytes() {
        constructor();

        getAsmValidatorMethodGeneratable().generate();

        return createBytes();
    }

    protected abstract AsmValidatorMethodGeneratable getAsmValidatorMethodGeneratable();

    abstract protected boolean createClassFileForDiagnose();

    private void createClassFileForDiagnose(byte[] bytes) {
        if (createClassFileForDiagnose())
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


    protected byte[] createBytes() {
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }


    protected void constructor() {
        val mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, p(Object.class),
                "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private ClassWriter createClassWriter() {
        int flags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        ClassWriter cw = new ClassWriter(flags);
        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, p(implName),
                null, p(Object.class), new String[]{p(AsmValidator.class)});

        return cw;
    }
}
