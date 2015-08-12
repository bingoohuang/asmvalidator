package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmUUID;

public class UUIDBean {
    @AsmUUID
    String uuid;

    @AsmUUID
    String uuid2;

    public String getUuid2() {
        return uuid2;
    }

    public void setUuid2(String uuid2) {
        this.uuid2 = uuid2;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
