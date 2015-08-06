package com.github.bingoohuang.asmvalidator.asm;

import java.util.concurrent.atomic.AtomicInteger;

public class LocalIndices {
    private final AtomicInteger localIndex; // 当前变量索引

    private int originalLocalIndex; // 原始本地变量索引
    private int stringLocalIndex; // 转换为string的本地变量索引
    private int stringLocalNullIndex; // 空判断布尔本地变量索引

    public LocalIndices(AtomicInteger localIndex) {
        this.localIndex = localIndex;
    }

    public int getOriginalLocalIndex() {
        return originalLocalIndex;
    }

    public int getStringLocalIndex() {
        return stringLocalIndex;
    }


    public int getStringLocalNullIndex() {
        return stringLocalNullIndex;
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

    public void incrementAndSetStringNullLocalIndex() {
        this.stringLocalNullIndex = localIndex.incrementAndGet();
    }
}
