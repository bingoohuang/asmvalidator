package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmEmail;
import lombok.Data;

@Data
public class EmailBean {
    @AsmEmail String email;
}
