package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import lombok.AllArgsConstructor;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public class AnnotationAndRoot {
    Annotation annotation;
    Annotation rootAnnotation;

    public Annotation ann() {
        return annotation;
    }

    public Annotation root() {
        if (rootAnnotation == null) return annotation;

        Class<? extends Annotation> annClass = annotation.annotationType();
        AsmConstraint asmConstraint = annClass.getAnnotation(AsmConstraint.class);
        if (!asmConstraint.allowMessageOverride()) return annotation;

        return rootAnnotation;
    }
}
