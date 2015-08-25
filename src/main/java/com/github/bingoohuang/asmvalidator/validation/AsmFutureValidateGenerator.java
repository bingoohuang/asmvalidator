package com.github.bingoohuang.asmvalidator.validation;

import static org.objectweb.asm.Opcodes.IFGT;

public class AsmFutureValidateGenerator extends AsmPastValidateGenerator {
    protected int compareOpCode() {
        return IFGT;
    }
}
