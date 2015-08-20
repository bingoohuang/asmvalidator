package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;

import java.lang.annotation.Annotation;

public class AnnotationAndRoot {
    Annotation annotation;
    Annotation rootAnnotation;


    public AnnotationAndRoot(Annotation annotation, Annotation rootAnnotation) {
        this.annotation = annotation;
        this.rootAnnotation = rootAnnotation;
    }

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
