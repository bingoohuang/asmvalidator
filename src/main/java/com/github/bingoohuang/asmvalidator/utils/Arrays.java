package com.github.bingoohuang.asmvalidator.utils;

public class Arrays {
    public static <T> boolean anyOf(T target, T... anys) {
        for (T a : anys) if (a.equals(target)) return true;
        return false;
    }
}
