package com.xg7plugins.xg7plugins.utils.reflection;

import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

@Getter
public class ReflectionObject {

    private Object object;
    private Class<?> objectClass;

    public ReflectionObject(Object object) {
        this.object = object;
        this.objectClass = object.getClass();
    }

    public static ReflectionObject of(Object object) {
        return new ReflectionObject(object);
    }

    @SneakyThrows
    public void setField(String name, Object value) {
        Field field = objectClass.getDeclaredField(name);
        field.setAccessible(true);
        field.set(object, value);
    }

    @SneakyThrows
    public <T> T getField(String name, Object value) {
        Field field = objectClass.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(object);
    }

    @SneakyThrows
    public ReflectionMethod getMethod(String name, Class<?>... parameterTypes) {
        return new ReflectionMethod(object, objectClass.getMethod(name, parameterTypes));
    }

}
