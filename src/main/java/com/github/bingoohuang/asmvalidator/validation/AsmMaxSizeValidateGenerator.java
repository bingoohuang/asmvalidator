package com.github.bingoohuang.asmvalidator.validation;

import com.github.bingoohuang.asmvalidator.AsmValidateGenerator;
import com.github.bingoohuang.asmvalidator.annotations.AsmMaxSize;
import com.github.bingoohuang.asmvalidator.asm.LocalIndices;
import com.github.bingoohuang.asmvalidator.utils.AnnotationAndRoot;
import com.github.bingoohuang.asmvalidator.utils.AsmValidators;
import com.github.bingoohuang.asmvalidator.utils.Asms;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Type;

import static com.github.bingoohuang.asmvalidator.utils.AsmValidators.addError;
import static org.objectweb.asm.Opcodes.*;

public class AsmMaxSizeValidateGenerator implements AsmValidateGenerator {
    @Override
    public void generateAsm(
            MethodVisitor mv, String fieldName, Class<?> fieldType,
            Type genericFieldType, AnnotationAndRoot annAndRoot, LocalIndices localIndices,
            String message,
            boolean checkBlank
    ) {
        mv.visitVarInsn(ILOAD, localIndices.getIsNullIndex());
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);

        AsmValidators.computeSize(mv, fieldType, localIndices);

        AsmMaxSize asmMaxSize = (AsmMaxSize) annAndRoot.ann();
        Asms.visitInt(mv, asmMaxSize.value());
        mv.visitJumpInsn(IF_ICMPLE, l1);
        addError(fieldName, fieldType, mv, annAndRoot, message, localIndices, l1);
    }

}
