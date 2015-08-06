package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidatorBadArgumentException;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.objectweb.asm.Opcodes.*;

public class AsmRangeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, LocalIndices localIndices) {
        AsmRange asmRange = (AsmRange) fieldAnnotation;
        String rangeExpression = StringUtils.trim(asmRange.value());

        if (isEmpty(rangeExpression)) return;

        Splitter splitter = Splitter.on(',').trimResults().omitEmptyStrings();
        List<String> rangeValues = splitter.splitToList(rangeExpression);
        if (rangeValues.size() < 2) throw error(fieldAnnotation);

        if (rangeValues.size() == 2) {
            if (tryRangeCheckGenerate(mv, field, fieldAnnotation, localIndices, rangeExpression, rangeValues)) return;
        }

        enumRangeCheckGenerate(mv, field, localIndices, rangeExpression, rangeValues);
    }

    private boolean tryRangeCheckGenerate(MethodVisitor mv, Field field,
                                          Annotation fieldAnnotation, LocalIndices localIndices,
                                          String rangeExpression, List<String> rangeValues) {
        String from = rangeValues.get(0);
        String to = rangeValues.get(1);

        char fromStart = from.charAt(0);
        char toEnd = to.charAt(to.length() - 1);

        boolean isRangeExpression = (fromStart == '[' || fromStart == '(') && (toEnd == ']' || toEnd == ')');
        if (!isRangeExpression) return false;

        from = StringUtils.trim(from.substring(1));
        to = StringUtils.trim(to.substring(0, to.length() - 1));

        if (isEmpty(from) && isEmpty(to)) throw error(fieldAnnotation);

        boolean includeFrom = fromStart == '[';
        boolean includeEnd = toEnd == ']';

        if (int.class == field.getType()) {
            intRangeCheckGenerate(mv, field, localIndices, rangeExpression,
                    from, to, includeFrom, includeEnd);
            return true;
        }

        if (String.class == field.getType()) {
            stringRangeCheckGenerate(mv, field, localIndices, rangeExpression,
                    from, to, includeFrom, includeEnd);
            return true;
        }

        throw new AsmValidatorBadArgumentException(fieldAnnotation + " is not support yet for " + field.getType());
    }

    private AsmValidatorBadArgumentException error(Annotation fieldAnnotation) {
        return new AsmValidatorBadArgumentException(fieldAnnotation + " is illegal");
    }

    private void enumRangeCheckGenerate(MethodVisitor mv, Field field, LocalIndices localIndices,
                                        String rangeExpression, List<String> rangeValues) {
        mv.visitTypeInsn(NEW, p(ArrayList.class));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(ArrayList.class), "<init>", "()V", false);
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
        addErr(mv, field, rangeExpression);
        mv.visitLabel(l1);
    }

    private void stringRangeCheckGenerate(MethodVisitor mv, Field field, LocalIndices localIndices,
                                          String rangeExpression,
                                          String from, String to,
                                          boolean includeFrom, boolean includeEnd) {
        if (isNotEmpty(from)) {
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            mv.visitLdcInsn(from);
            compareStringValue(mv, field, rangeExpression, includeFrom);
        }

        if (isNotEmpty(to)) {
            mv.visitLdcInsn(to);
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            compareStringValue(mv, field, rangeExpression, includeEnd);
        }
    }

    private void intRangeCheckGenerate(MethodVisitor mv, Field field, LocalIndices localIndices,
                                       String rangeExpression,
                                       String from, String to,
                                       boolean includeFrom, boolean includeEnd) {
        if (isNotEmpty(from)) {
            mv.visitVarInsn(ILOAD, localIndices.getOriginalLocalIndex());
            Asms.visitInt(mv, Integer.parseInt(from));
            compareValue(mv, field, rangeExpression, includeFrom);
        }
        if (isNotEmpty(to)) {
            Asms.visitInt(mv, Integer.parseInt(to));
            mv.visitVarInsn(ILOAD, localIndices.getOriginalLocalIndex());
            compareValue(mv, field, rangeExpression, includeEnd);
        }
    }

    private void compareStringValue(MethodVisitor mv, Field field,
                                    String rangeExpression, boolean includeEnd) {
        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class),
                "compareTo", sig(int.class, String.class), false);
        Label label = new Label();
        mv.visitJumpInsn(includeEnd ? IFGE : IFGT, label);
        addErr(mv, field, rangeExpression);
        mv.visitLabel(label);
    }

    private void compareValue(MethodVisitor mv, Field field,
                              String rangeExpression, boolean includeBoundary) {
        Label label = new Label();
        mv.visitJumpInsn(includeBoundary ? IF_ICMPGE : IF_ICMPGT, label);
        addErr(mv, field, rangeExpression);
        mv.visitLabel(label);
    }

    private void addErr(MethodVisitor mv, Field field, String rangeExpression) {
        AsmValidators.newValidatorError(mv);
        mv.visitLdcInsn(field.getName());
        mv.visitLdcInsn("取值不在范围" + rangeExpression + "内");
        AsmValidators.addError(mv);
    }

}
