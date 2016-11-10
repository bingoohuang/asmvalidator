package com.github.bingoohuang.asmvalidator.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;

@UtilityClass
public class AsmConstraintCache {
    private Cache<String, Annotation>
            cache = CacheBuilder.newBuilder().build();

    public void put(String hashCode, Annotation ann) {
        cache.put(hashCode, ann);
    }

    public Annotation get(String hashCode) {
        return cache.getIfPresent(hashCode);
    }
}
