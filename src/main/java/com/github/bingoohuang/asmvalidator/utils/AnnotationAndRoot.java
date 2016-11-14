package com.github.bingoohuang.asmvalidator.utils;

import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import lombok.AllArgsConstructor;
import lombok.val;

import java.lang.annotation.Annotation;

@AllArgsConstructor public class AnnotationAndRoot {
    Annotation annotation;
    Annotation rootAnnotation;

    public AnnotationAndRoot(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation ann() {
        return annotation;
    }

    public Annotation root() {
        if (rootAnnotation == null) return annotation;

        val annClass = annotation.annotationType();
        val asmConstraint = annClass.getAnnotation(AsmConstraint.class);
        if (!asmConstraint.allowMessageOverride()) return annotation;

        return rootAnnotation;
    }
}
