package com.xg7plugins.xg7plugins.utils;

import com.xg7plugins.xg7plugins.Plugin;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * This class is used to debug
 */
public class Log {

    public static void severe(Plugin plugin, String message) {
        Bukkit.getLogger().severe("[" + plugin.getName()  + " ERROR] " + message);
    }

    public static void fine(Plugin plugin, String message) {
        if (plugin.log()) Bukkit.getLogger().fine("[" + plugin.getName()  + " SUCCESS] " + message);
    }

    public static void info(Plugin plugin, String message) {
        if (plugin.log()) Bukkit.getLogger().info("[" + plugin.getName() + " DEBUG] " + message);
    }

    public static void warn(Plugin plugin, String message) {
        Bukkit.getLogger().log(Level.WARNING, "[" + plugin.getName() + " ALERT] " + message);
    }

    public static void loading(Plugin plugin, String message) {
        Bukkit.getLogger().info( "[" + plugin.getName() + "]" + message);
    }

}
