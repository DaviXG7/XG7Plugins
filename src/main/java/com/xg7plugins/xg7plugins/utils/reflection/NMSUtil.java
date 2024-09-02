package com.xg7plugins.xg7plugins.utils.reflection;

import lombok.Getter;
import org.bukkit.Bukkit;

public class NMSUtil {

    private static final String packageName = Bukkit.getServer().getClass().getPackage().getName();
    @Getter
    private static final String version = packageName.substring(packageName.lastIndexOf('.') + 1);


    public static ReflectionClass getNMSClass(String className) {
        String fullName = "net.minecraft.server." + version + "." + className;
        return ReflectionClass.of(fullName);
    }

    public static ReflectionClass getCraftBukkitClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + version + "." + className;
        return ReflectionClass.of(fullName);
    }

}
