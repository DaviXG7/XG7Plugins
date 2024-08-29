package com.xg7plugins.xg7plugins.commands.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubCommandConfig {
    String name() default "";
    String perm() default "default";
    boolean isOnlyInWorld() default false;
    boolean isOnlyPlayer() default false;
}
