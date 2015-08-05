package com.github.bingoohuang.asmvalidator.asm;

import com.github.bingoohuang.asmvalidator.annotations.AsmNotBlank;
import com.github.bingoohuang.asmvalidator.annotations.AsmSize;
import com.github.bingoohuang.asmvalidator.impl.Consts;

public interface AsmDefaultAnnotations {
    @AsmNotBlank
    @AsmSize(max = Consts.DEFAULT_MAX_SIZE)
    void asmDefaultAnnotations();
}
