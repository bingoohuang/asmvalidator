package com.github.bingoohuang.asmvalidator.validator.tests;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class MyTemplateMain {
    public static void main(String[] args) {
        Class<?> clazz = MyTemplate.class;

        Type type = clazz.getGenericInterfaces()[0];
        ParameterizedType pType = (ParameterizedType) type;
        Type argType = pType.getActualTypeArguments()[0];
        System.out.println(argType);
        // class com.github.bingoohuang.asmvalidator.validator.tests.Bingoo

        Method method = clazz.getMethods()[0];
        Type returnType = method.getGenericReturnType();
        ParameterizedType pReturnType = (ParameterizedType) returnType;
        Type argReturnType = pReturnType.getActualTypeArguments()[0];
        System.out.println(argReturnType);
        // T
    }

    public interface MyTemplate extends Template<Bingoo> {
    }

    public interface Template<T> {
        List<T> visit();
    }

    public static class Bingoo {
    }
}
