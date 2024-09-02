package com.xg7plugins.xg7plugins.utils.reflection;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@AllArgsConstructor
public class ReflectionMethod {

    private Object object;
    private Method method;

    @SneakyThrows
    public Object invoke(Object... args) {
        return this.method.invoke(object, args);
    }
    @SneakyThrows
    public ReflectionObject invokeToRObject(Object... args) {
        return new ReflectionObject(this.method.invoke(object, args));
    }

    @SneakyThrows
    public static ReflectionMethod of(Object object, String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(object, object.getClass().getMethod(name, parameterTypes));
    }

    @SneakyThrows
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

}
