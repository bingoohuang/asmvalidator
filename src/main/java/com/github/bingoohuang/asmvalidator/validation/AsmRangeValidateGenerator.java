package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateBadArgException;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import com.google.common.base.Splitter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.objectweb.asm.Opcodes.*;

public class AsmRangeValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot, LocalIndices localIndices,
            String message,
            boolean checkBlank) {
        AsmRange asmRange = (AsmRange) annAndRoot.ann();
        String rangeExpression = StringUtils.trim(asmRange.value());

        if (isEmpty(rangeExpression)) return;

        val splitter = Splitter.on(',').trimResults().omitEmptyStrings();
        val rangeValues = splitter.splitToList(rangeExpression);
        if (rangeValues.size() < 2) throw error(annAndRoot);

        if (rangeValues.size() == 2) {
            if (tryRangeCheck(mv, fieldName, fieldType, annAndRoot,
                    localIndices, rangeValues, message)) return;
        }

        enumsCheck(mv, fieldName, fieldType, localIndices, rangeValues,
                message, annAndRoot);
    }

    private boolean tryRangeCheck(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            AnnotationAndRoot annAndRoot,
            LocalIndices localIndices,
            List<String> rangeValues,
            String message
    ) {
        String from = rangeValues.get(0);
        String to = rangeValues.get(1);

        char fromStart = from.charAt(0);
        char toEnd = to.charAt(to.length() - 1);

        boolean isRangeExpression
                = (fromStart == '[' || fromStart == '(')
                && (toEnd == ']' || toEnd == ')');
        if (!isRangeExpression) return false;

        from = StringUtils.trim(from.substring(1));
        to = StringUtils.trim(to.substring(0, to.length() - 1));

        if (isEmpty(from) && isEmpty(to)) throw error(annAndRoot);

        boolean includeFrom = fromStart == '[';
        boolean includeEnd = toEnd == ']';

        if (Integer.TYPE == fieldType || Integer.class == fieldType) {
            intRangeCheckGenerate(mv, fieldName, fieldType, localIndices,
                    from, to, includeFrom, includeEnd,
                    message, annAndRoot);
            return true;
        }

        if (Float.TYPE == fieldType || Float.class == fieldType) {
            floatRangeCheckGenerate(mv, fieldName, fieldType, localIndices,
                    from, to, includeFrom, includeEnd,
                    message, annAndRoot);
            return true;
        }

        if (Long.TYPE == fieldType || Long.class == fieldType) {
            longRangeCheckGenerate(mv, fieldName, fieldType, localIndices,
                    from, to, includeFrom, includeEnd,
                    message, annAndRoot);
            return true;
        }

        if (String.class == fieldType) {
            stringRangeCheckGenerate(mv, fieldName, fieldType, localIndices,
                    from, to, includeFrom, includeEnd,
                    message, annAndRoot);
            return true;
        }

        throw new AsmValidateBadArgException(annAndRoot.ann()
                + " is not support yet for " + fieldType);
    }

    private AsmValidateBadArgException error(AnnotationAndRoot annAndRoot) {
        return new AsmValidateBadArgException(annAndRoot.ann() + " is illegal");
    }

    private void enumsCheck(
            MethodVisitor mv, String fieldName,
            Class<?> fieldType,
            LocalIndices localIndices,
            List<String> rangeValues,
            String msg,
            AnnotationAndRoot fieldAnn
    ) {
        mv.visitTypeInsn(NEW, p(ArrayList.class));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(ArrayList.class),
                "<init>", "()V", false);
        localIndices.incrementLocalIndex();
        mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());

        for (String value : rangeValues) {
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            mv.visitLdcInsn(value);
            mv.visitMethodInsn(INVOKEINTERFACE, p(List.class),
                    "add", sig(boolean.class, Object.class), true);
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitMethodInsn(INVOKEINTERFACE, p(List.class),
                "contains", sig(boolean.class, Object.class), true);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        addError(fieldName, fieldType, mv, fieldAnn, msg, localIndices, l1);
    }

    private void stringRangeCheckGenerate(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            LocalIndices localIndices,
            String from, String to,
            boolean includeFrom, boolean includeEnd,
            String message,
            AnnotationAndRoot annAndRoot
    ) {

        if (isNotEmpty(from)) {
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            mv.visitLdcInsn(from);
            compareStringValue(mv, fieldName, fieldType, includeFrom,
                    message, annAndRoot, localIndices);
        }

        if (isNotEmpty(to)) {
            mv.visitLdcInsn(to);
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            compareStringValue(mv, fieldName, fieldType, includeEnd,
                    message, annAndRoot, localIndices);
        }
    }

    private void intRangeCheckGenerate(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            LocalIndices localIndices,
            String from, String to,
            boolean includeFrom, boolean includeEnd,
            String message,
            AnnotationAndRoot annAndRoot
    ) {
        int intIndex = 0;
        if (isNotEmpty(from) || isNotEmpty(to)) {
            if (localIndices.isOriginalPrimitive()) {
                intIndex = localIndices.getOriginalLocalIndex();
            } else {
                mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
                mv.visitMethodInsn(INVOKEVIRTUAL, p(Integer.class),
                        "intValue", sig(int.class), false);
                intIndex = localIndices.incrementLocalIndex();
                mv.visitVarInsn(ISTORE, intIndex);
            }
        }

        if (isNotEmpty(from)) {
            mv.visitVarInsn(ILOAD, intIndex);
            Asms.visitInt(mv, Integer.parseInt(from));
            compareValue(mv, fieldName, fieldType, includeFrom ? IF_ICMPGE : IF_ICMPGT,
                    message, annAndRoot, localIndices);
        }
        if (isNotEmpty(to)) {
            Asms.visitInt(mv, Integer.parseInt(to));
            mv.visitVarInsn(ILOAD, intIndex);
            compareValue(mv, fieldName, fieldType, includeEnd ? IF_ICMPGE : IF_ICMPGT,
                    message, annAndRoot, localIndices);
        }
    }

    private void floatRangeCheckGenerate(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            LocalIndices localIndices,
            String from, String to,
            boolean includeFrom, boolean includeEnd,
            String message,
            AnnotationAndRoot annAndRoot
    ) {
        int floatIndex = 0;
        if (isNotEmpty(from) || isNotEmpty(to)) {
            if (localIndices.isOriginalPrimitive()) {
                floatIndex = localIndices.getOriginalLocalIndex();
            } else {
                mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
                mv.visitMethodInsn(INVOKEVIRTUAL, p(Float.class),
                        "floatValue", sig(float.class), false);
                floatIndex = localIndices.incrementLocalIndex();
                mv.visitVarInsn(FSTORE, floatIndex);
            }
        }

        if (isNotEmpty(from)) {
            mv.visitVarInsn(FLOAD, floatIndex);
            mv.visitLdcInsn(new Float(from));
            mv.visitInsn(FCMPG);
            compareValue(mv, fieldName, fieldType, includeFrom ? IFGE : IFGT,
                    message, annAndRoot, localIndices);
        }
        if (isNotEmpty(to)) {
            mv.visitLdcInsn(new Float(to));
            mv.visitVarInsn(FLOAD, floatIndex);
            mv.visitInsn(FCMPL);
            compareValue(mv, fieldName, fieldType, includeEnd ? IFGE : IFGT,
                    message, annAndRoot, localIndices);
        }
    }

    private void longRangeCheckGenerate(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            LocalIndices localIndices,
            String from, String to,
            boolean includeFrom, boolean includeEnd,
            String message,
            AnnotationAndRoot annAndRoot
    ) {
        int longIndex = 0;
        if (isNotEmpty(from) || isNotEmpty(to)) {
            if (localIndices.isOriginalPrimitive()) {
                longIndex = localIndices.getOriginalLocalIndex();
            } else {
                mv.visitVarInsn(ALOAD, localIndices.getOriginalLocalIndex());
                mv.visitMethodInsn(INVOKEVIRTUAL, p(Long.class),
                        "longValue", sig(long.class), false);
                longIndex = localIndices.incrementLocalIndex();
                mv.visitVarInsn(LSTORE, longIndex);
            }
        }

        if (isNotEmpty(from)) {
            mv.visitVarInsn(LLOAD, longIndex);
            mv.visitLdcInsn(new Long(from));
            mv.visitInsn(LCMP);
            compareValue(mv, fieldName, fieldType, includeFrom ? IFGE : IFGT,
                    message, annAndRoot, localIndices);
        }
        if (isNotEmpty(to)) {
            mv.visitLdcInsn(new Long(to));
            mv.visitVarInsn(LLOAD, longIndex);
            mv.visitInsn(LCMP);
            compareValue(mv, fieldName, fieldType, includeEnd ? IFGE : IFGT,
                    message, annAndRoot, localIndices);
        }
    }

    private void compareStringValue(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            boolean includeEnd,
            String msg,
            AnnotationAndRoot annAndRoot,
            LocalIndices localIndices
    ) {
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class),
                "compareTo", sig(int.class, String.class), false);
        Label label = new Label();
        mv.visitJumpInsn(includeEnd ? IFGE : IFGT, label);
        addError(fieldName, fieldType, mv, annAndRoot, msg, localIndices, label);
    }

    private void compareValue(
            MethodVisitor mv,
            String fieldName,
            Class<?> fieldType,
            int jumpInsnCode,
            String msg,
            AnnotationAndRoot annAndRoot,
            LocalIndices localIndices
    ) {
        Label label = new Label();
        mv.visitJumpInsn(jumpInsnCode, label);
        addError(fieldName, fieldType, mv, annAndRoot, msg, localIndices, label);
    }
}
