package com.github.bingoohuang.asmvalidator.asm;

public class AsmValidatorClassLoader extends ClassLoader {
    public AsmValidatorClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
