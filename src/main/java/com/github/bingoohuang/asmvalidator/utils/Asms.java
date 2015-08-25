package com.github.bingoohuang.asmvalidator.utils;

import com.google.common.primitives.Primitives;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class Asms {
    public static void wrapPrimitive(MethodVisitor mv, Class<?> type) {
        if (type.isPrimitive()) {
            Class<?> wrap = Primitives.wrap(type);
            mv.visitMethodInsn(INVOKESTATIC, p(wrap),
                    "valueOf", sig(wrap, type), false);
        }
    }

    // Creates a dotted class name from a path/package name
    public static String c(String p) {
        return p.replace('/', '.');
    }

    // Creates a class path name, from a Class.
    public static String p(Class n) {
        return n.getName().replace('.', '/');
    }

    public static String p(String className) {
        return className.replace('.', '/');
    }


    // Creates a class identifier of form Labc/abc;, from a Class.
    public static String ci(Class n) {
        if (n.isArray()) {
            n = n.getComponentType();
            if (n.isPrimitive()) {
                if (n == Byte.TYPE) {
                    return "[B";
                } else if (n == Boolean.TYPE) {
                    return "[Z";
                } else if (n == Short.TYPE) {
                    return "[S";
                } else if (n == Character.TYPE) {
                    return "[C";
                } else if (n == Integer.TYPE) {
                    return "[I";
                } else if (n == Float.TYPE) {
                    return "[F";
                } else if (n == Double.TYPE) {
                    return "[D";
                } else if (n == Long.TYPE) {
                    return "[J";
                } else {
                    throw new RuntimeException("Unrecognized type in compiler: " + n.getName());
                }
            } else {
                return "[" + ci(n);
            }
        } else {
            if (n.isPrimitive()) {
                if (n == Byte.TYPE) {
                    return "B";
                } else if (n == Boolean.TYPE) {
                    return "Z";
                } else if (n == Short.TYPE) {
                    return "S";
                } else if (n == Character.TYPE) {
                    return "C";
                } else if (n == Integer.TYPE) {
                    return "I";
                } else if (n == Float.TYPE) {
                    return "F";
                } else if (n == Double.TYPE) {
                    return "D";
                } else if (n == Long.TYPE) {
                    return "J";
                } else if (n == Void.TYPE) {
                    return "V";
                } else {
                    throw new RuntimeException("Unrecognized type in compiler: " + n.getName());
                }
            } else {
                return "L" + p(n) + ";";
            }
        }
    }

    // Create a method signature from the given param types and return values
    public static String sig(Class retval, Class... params) {
        return sigParams(params) + ci(retval);
    }

    public static String sig(Class[] retvalParams) {
        Class[] justParams = new Class[retvalParams.length - 1];
        System.arraycopy(retvalParams, 1, justParams, 0, justParams.length);
        return sigParams(justParams) + ci(retvalParams[0]);
    }

    public static String sig(Class retval, String descriptor, Class... params) {
        return sigParams(descriptor, params) + ci(retval);
    }

    public static String sigParams(Class... params) {
        StringBuilder signature = new StringBuilder("(");

        for (int i = 0; i < params.length; i++) {
            signature.append(ci(params[i]));
        }

        signature.append(")");

        return signature.toString();
    }

    public static String sigParams(String descriptor, Class... params) {
        StringBuilder signature = new StringBuilder("(");

        signature.append(descriptor);

        for (int i = 0; i < params.length; i++) {
            signature.append(ci(params[i]));
        }

        signature.append(")");

        return signature.toString();
    }

    public static void visitInt(MethodVisitor mv, int value) {
        if (value >= 0 && value <= 5)
            // iconst_n is defined for n from 0 to 5
            mv.visitInsn(ICONST_0 + value);
        else if (value >= -128 && value <= 127)
            // // bipush can push constant values between -128 and 127. It is a two-byte instruction.
            mv.visitIntInsn(BIPUSH, value);
        else {
            // sipush: push a two-byte signed integer (-32768 to 32767)
            mv.visitIntInsn(SIPUSH, value);
        }
    }

    public static int storeOpCode(Class<?> type) {
        if (!type.isPrimitive()) return ASTORE;
        if (type == int.class) return ISTORE;
        if (type == long.class) return LSTORE;
        throw new RuntimeException("not supported now");
    }

    public static int loadOpCode(Class<?> type) {
        if (!type.isPrimitive()) return ALOAD;
        if (type == int.class) return ILOAD;
        if (type == long.class) return LLOAD;

        throw new RuntimeException("not supported now");
    }
}
