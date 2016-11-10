package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmUUID;
import lombok.Data;

@Data
public class UUIDBean {
    @AsmUUID String uuid;
    @AsmUUID String uuid2;
}
