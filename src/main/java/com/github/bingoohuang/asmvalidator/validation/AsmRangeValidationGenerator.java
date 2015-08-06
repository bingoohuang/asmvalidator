package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.ex.AsmValidatorAnnotationBadArgumentException;
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
        if (rangeValues.size() == 2) {
            String from = rangeValues.get(0);
            String to = rangeValues.get(1);
            char fromStart = from.charAt(0);
            char toEnd = to.charAt(to.length() - 1);
            if ((fromStart == '[' || fromStart == '(') || (toEnd == ']' || toEnd == ')')) {
                boolean includeFrom = fromStart == '[';
                boolean includeEnd = toEnd == ']';
                from = StringUtils.trim(from.substring(1));
                to = StringUtils.trim(to.substring(0, to.length() - 1));
                if (isEmpty(from) && isEmpty(to))
                    throw new AsmValidatorAnnotationBadArgumentException(fieldAnnotation + " is illegal");

                if (int.class == field.getType()) {
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

                    return;
                }

                if (String.class == field.getType()) {
                    mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
                    mv.visitLdcInsn(from);
                    mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "compareTo", sig(int.class, String.class), false);
                    Label l1 = new Label();
                    mv.visitJumpInsn(includeFrom ? IFLT : IFLE, l1);
                    mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
                    mv.visitLdcInsn(to);
                    mv.visitMethodInsn(INVOKEVIRTUAL, p(String.class), "compareTo", sig(int.class, String.class), false);
                    Label l2 = new Label();
                    mv.visitJumpInsn(includeEnd ? IFLE : IFLT, l2);
                    mv.visitLabel(l1);

                    addErr(mv, field, rangeExpression);
                    mv.visitLabel(l2);
                    return;
                }
            }
        }

        if (rangeValues.size() < 2)
            throw new AsmValidatorAnnotationBadArgumentException("the argument for annotation is illegal");

        mv.visitTypeInsn(NEW, p(ArrayList.class));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, p(ArrayList.class), "<init>", "()V", false);
        localIndices.incrementLocalIndex();
        mv.visitVarInsn(ASTORE, localIndices.getLocalIndex());

        for (String value : rangeValues) {
            mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
            mv.visitLdcInsn(value);
            mv.visitMethodInsn(INVOKEINTERFACE, p(List.class), "add", sig(boolean.class, Object.class), true);
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, localIndices.getLocalIndex());
        mv.visitVarInsn(ALOAD, localIndices.getStringLocalIndex());
        mv.visitMethodInsn(INVOKEINTERFACE, p(List.class), "contains", sig(boolean.class, Object.class), true);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        addErr(mv, field, rangeExpression);
        mv.visitLabel(l1);
    }

    private void compareValue(MethodVisitor mv, Field field, String rangeExpression, boolean includeBoundary) {
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
