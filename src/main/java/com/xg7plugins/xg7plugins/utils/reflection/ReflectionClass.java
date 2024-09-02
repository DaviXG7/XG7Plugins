package com.xg7plugins.xg7plugins.utils.reflection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

@RequiredArgsConstructor
public class ReflectionClass {

    @Getter
    private final Class<?> aClass;
    private Constructor<?> constructor;

    @SneakyThrows
    public ReflectionObject newInstance(Object... args) {
        if (constructor == null) return new ReflectionObject(aClass.getConstructor().newInstance());
        return new ReflectionObject(constructor.newInstance(args));
    }

    @SneakyThrows
    public ReflectionClass getConstructor(Class<?>... parameterTypes) {
        constructor = aClass.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return this;
    }

    @Contract("_ -> new")
    @SneakyThrows
    public static @NotNull ReflectionClass of(String name) {
        return new ReflectionClass(Class.forName(name));
    }
    @Contract("_ -> new")
    @SneakyThrows
    public static @NotNull ReflectionClass of(Class<?> clazz) {
        return new ReflectionClass(clazz);
    }

    public Object cast(Object o) {
        return aClass.cast(o);
    }

    public ReflectionObject castToRObject(Object o) {
        return new ReflectionObject(aClass.cast(o));
    }
    @SneakyThrows
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return aClass.getAnnotation(annotationClass);
    }

}
