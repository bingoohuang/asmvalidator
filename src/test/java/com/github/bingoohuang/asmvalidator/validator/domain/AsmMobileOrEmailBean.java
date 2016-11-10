package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmMobileOrEmail;
import lombok.Data;

@Data
public class AsmMobileOrEmailBean {
    @AsmMobileOrEmail String mobileOrEmail;
}
