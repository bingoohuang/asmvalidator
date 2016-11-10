package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.annotations.AsmMaxSize;
import com.github.bingoohuang.asmvalidator.annotations.AsmNotBlank;

public interface AsmDefaultAnnotations {
    @AsmNotBlank
    @AsmMaxSize(AsmConsts.DEFAULT_MAX_SIZE)
    void asmDefaultAnnotations();
}
