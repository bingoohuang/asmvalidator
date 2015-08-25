package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmSize;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static org.objectweb.asm.Opcodes.*;

public class AsmSizeValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            AnnotationAndRoot annAndRoot, LocalIndices localIndices,
            String message
    ) {
        mv.visitVarInsn(ILOAD, localIndices.getIsNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);

        AsmValidators.computeSize(mv, fieldType, localIndices);

        AsmSize asmSize = (AsmSize) annAndRoot.ann();
        Asms.visitInt(mv, asmSize.value());
        Label l2 = new Label();
        mv.visitJumpInsn(getIfCompareOpCode(), l2);
        mv.visitLabel(l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l2);
    }

    private int getIfCompareOpCode() {
        return IF_ICMPEQ;
    }


}
