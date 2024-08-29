package com.xg7plugins.xg7plugins.utils.reflection;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class ReflectionClass {

    private final Class<?> aClass;
    private Object o = null;

    @SneakyThrows
    public void newInstance(Object... args) {
        if (o == null) o = aClass.getConstructor().newInstance(args);
    }

    @SneakyThrows
    public static ReflectionClass of(String name) {
        return new ReflectionClass(Class.forName(name));
    }

}
