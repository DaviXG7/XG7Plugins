package com.xg7plugins.api.commandsmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface CommandConfig {
    String enabledIf() default "";
    boolean isOnlyInWorld() default false;
    boolean isOnlyPlayer() default false;
}
