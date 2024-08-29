package com.xg7plugins.xg7plugins.utils.reflection;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class ReflectionUtils {

    @SneakyThrows
    public Field getDeclaredField(Class<?> clazz, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    @SneakyThrows
    public void set(Field field, Object fieldObject, Object o) {
        field.set(fieldObject, o);
    }

}
