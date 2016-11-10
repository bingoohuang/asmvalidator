package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmMobile;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AsmValid @Data @AllArgsConstructor @NoArgsConstructor
public class MobileBean {
    @AsmMobile String mobile;
}
