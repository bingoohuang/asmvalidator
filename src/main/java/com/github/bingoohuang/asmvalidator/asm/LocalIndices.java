package com.github.bingoohuang.asmvalidator.asm;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

public class LocalIndices {
    final AtomicInteger localIndex; // 当前变量索引

    @Setter @Getter int originalLocalIndex = 1; // 原始本地变量索引
    @Setter @Getter boolean originalPrimitive; // 原始变量是否是原生类型
    @Getter int stringLocalIndex = 1; // 转换为string的本地变量索引
    @Getter int isNullIndex; // 空判断布尔本地变量索引

    public LocalIndices(AtomicInteger localIndex) {
        this.localIndex = localIndex;
    }

    public int getLocalIndex() {
        return localIndex.get();
    }

    public void incrementAndSetOriginalLocalIndex() {
        this.originalLocalIndex = localIndex.incrementAndGet();
        this.stringLocalIndex = this.originalLocalIndex;
    }

    public void incrementAndSetStringLocalIndex() {
        this.stringLocalIndex = localIndex.incrementAndGet();
    }

    public void incrementAndSetNullLocalIndex() {
        this.isNullIndex = localIndex.incrementAndGet();
    }

    public int incrementLocalIndex() {
        return this.localIndex.incrementAndGet();
    }
}
