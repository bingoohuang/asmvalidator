package com.github.bingoohuang.asmvalidator.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.lang.annotation.Annotation;

public class AsmConstraintCache {
    private static Cache<String, Annotation>
            cache = CacheBuilder.newBuilder().build();

    public static void put(String hashCode, Annotation ann) {
        cache.put(hashCode, ann);
    }

    public static Annotation get(String hashCode) {
        return cache.getIfPresent(hashCode);
    }
}
