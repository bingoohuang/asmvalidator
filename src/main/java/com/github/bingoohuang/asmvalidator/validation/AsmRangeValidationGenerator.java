package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidatorBadArgException;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.objectweb.asm.Opcodes.*;

public class AsmRangeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, Field field,
            Annotation fieldAnnotation, LocalIndices localIndices,
            AsmConstraint constraint, String message) {
        AsmRange asmRange = (AsmRange) fieldAnnotation;
        String rangeExpression = StringUtils.trim(asmRange.value());

        if (isEmpty(rangeExpression)) return;

        Splitter splitter = Splitter.on(',').trimResults().omitEmptyStrings();
        List<String> rangeValues = splitter.splitToList(rangeExpression);
        if (rangeValues.size() < 2) throw error(fieldAnnotation);

        if (rangeValues.size() == 2) {
            if (tryRangeCheck(mv, field, fieldAnnotation,
                    localIndices, rangeValues, constraint, message)) return;
        }

        enumsCheck(mv, field, localIndices, rangeValues, constraint, message,
                fieldAnnotation);
    }

    private boolean tryRangeCheck(
            MethodVisitor mv, Field field, Annotation fieldAnnotation,
            LocalIndices localIndices, List<String> rangeValues,
            AsmConstraint constraint, String message
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

        if (isEmpty(from) && isEmpty(to)) throw error(fieldAnnotation);

        boolean includeFrom = fromStart == '[';
        boolean includeEnd = toEnd == ']';

        if (int.class == field.getType()) {
            intRangeCheckGenerate(mv, field, localIndices,
                    from, to, includeFrom, includeEnd,
                    constraint, message, fieldAnnotation);
            return true;
        }

        if (String.class == field.getType()) {
            stringRangeCheckGenerate(mv, field, localIndices,
                    from, to, includeFrom, includeEnd,
                    constraint, message, fieldAnnotation);
            return true;
        }

        throw new AsmValidatorBadArgException(fieldAnnotation
                + " is not support yet for " + field.getType());
    }

    private AsmValidatorBadArgException error(Annotation fieldAnnotation) {
        return new AsmValidatorBadArgException(fieldAnnotation + " is illegal");
    }

    private void enumsCheck(
            MethodVisitor mv, Field field, LocalIndices localIndices,
            List<String> rangeValues,
            AsmConstraint constraint, String message,
            Annotation fieldAnnotation
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
        addError(field.getName(), mv, fieldAnnotation, constraint, message, localIndices);
        mv.visitLabel(l1);
    }

    private void stringRangeCheckGenerate(
            MethodVisitor mv, Field field,
            LocalIndices localIndices,
            String from, String to,
            boolean includeFrom, boolean includeEnd,
            AsmConstraint constraint, String message,
            Annotation fieldAnnotation
    ) {

        if (isNotEmpty(from)) {
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            mv.visitLdcInsn(from);
            compareStringValue(mv, field, includeFrom, constraint,
                    message, fieldAnnotation, localIndices);
        }

        if (isNotEmpty(to)) {
            mv.visitLdcInsn(to);
            mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
            compareStringValue(mv, field, includeEnd, constraint,
                    message, fieldAnnotation, localIndices);
        }
    }

    private void intRangeCheckGenerate(
            MethodVisitor mv, Field field,
            LocalIndices localIndices,
            String from, String to,
            boolean includeFrom, boolean includeEnd,
            AsmConstraint constraint, String message,
            Annotation fieldAnnotation
    ) {

        if (isNotEmpty(from)) {
            mv.visitVarInsn(ILOAD, localIndices.getOriginalLocalIndex());
            Asms.visitInt(mv, Integer.parseInt(from));
            compareValue(mv, field, includeFrom, constraint,
                    message, fieldAnnotation, localIndices);
        }
        if (isNotEmpty(to)) {
            Asms.visitInt(mv, Integer.parseInt(to));
            mv.visitVarInsn(ILOAD, localIndices.getOriginalLocalIndex());
            compareValue(mv, field, includeEnd, constraint,
                    message, fieldAnnotation, localIndices);
        }
    }

    private void compareStringValue(
            MethodVisitor mv, Field field,
            boolean includeEnd,
            AsmConstraint constraint, String message,
            Annotation fieldAnnotation, LocalIndices localIndices
    ) {

        mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class),
                "compareTo", sig(int.class, String.class), false);
        Label label = new Label();
        mv.visitJumpInsn(includeEnd ? IFGE : IFGT, label);
        addError(field.getName(), mv, fieldAnnotation, constraint, message,
                localIndices);
        mv.visitLabel(label);
    }

    private void compareValue(
            MethodVisitor mv, Field field,
            boolean includeBoundary,
            AsmConstraint constraint, String message,
            Annotation fieldAnnotation, LocalIndices localIndices
    ) {
        Label label = new Label();
        mv.visitJumpInsn(includeBoundary ? IF_ICMPGE : IF_ICMPGT, label);
        addError(field.getName(), mv, fieldAnnotation, constraint, message,
                localIndices);
        mv.visitLabel(label);
    }


}
