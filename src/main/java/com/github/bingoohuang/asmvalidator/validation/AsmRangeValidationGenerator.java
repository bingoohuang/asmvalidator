package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidationGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmRange;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import static com.github.bingoohuang.asmvalidator.utils.Asms.p;
import static com.github.bingoohuang.asmvalidator.utils.Asms.sig;
import static org.objectweb.asm.Opcodes.*;

public class AsmRangeValidationGenerator implements AsmValidationGenerator {
    @Override
    public void generateAsm(MethodVisitor mv, Field field, Annotation fieldAnnotation, LocalIndices localIndices) {
        AsmRange asmRange = (AsmRange) fieldAnnotation;
        String rangeExpression = StringUtils.trim(asmRange.value());

        if (StringUtils.isEmpty(rangeExpression)) return;

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

                if (int.class == field.getType()) {
                    mv.visitVarInsn(ILOAD, localIndices.getOriginalLocalIndex());
                    Asms.visitInt(mv, Integer.parseInt(from));
                    Label l1 = new Label();
                    mv.visitJumpInsn(includeFrom ? IF_ICMPLT : IF_ICMPLE, l1);
                    mv.visitVarInsn(ILOAD, localIndices.getOriginalLocalIndex());
                    Asms.visitInt(mv, Integer.parseInt(to));
                    Label l2 = new Label();
                    mv.visitJumpInsn(includeEnd ? IF_ICMPLE : IF_ICMPLT, l2);
                    mv.visitLabel(l1);

                    AsmValidators.newValidatorError(mv);
                    mv.visitLdcInsn(field.getName());
                    mv.visitLdcInsn("取值不在范围" + rangeExpression + "内");
                    AsmValidators.addError(mv);
                    mv.visitLabel(l2);
                } else if (String.class == field.getType()) {
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

                    AsmValidators.newValidatorError(mv);
                    mv.visitLdcInsn(field.getName());
                    mv.visitLdcInsn("取值不在范围" + rangeExpression + "内");
                    AsmValidators.addError(mv);
                    mv.visitLabel(l2);
                }
            }
        }

    }

}
