package com.github.bingoohuang.asmvalidator.asm;

public class LocalIndices {
    private int localIndex; // 当前变量索引

    private int originalLocalIndex; // 原始本地变量索引
    private int stringLocalIndex; // 转换为string的本地变量索引
    private int stringLocalNullIndex; // 空判断布尔本地变量索引

    public LocalIndices(int localIndex) {
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
        return localIndex;
    }

    public void incrementAndSetOriginalLocalIndex() {
        this.originalLocalIndex = ++localIndex;
        this.stringLocalIndex = this.originalLocalIndex;
    }

    public void incrementAndSetStringLocalIndex() {
        this.stringLocalIndex = ++localIndex;
    }

    public void incrementAndSetStringNullLocalIndex() {
        this.stringLocalNullIndex = ++localIndex;
    }
}
