package com.xg7plugins.xg7plugins.utils;

import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * This class is used to debug
 */
@AllArgsConstructor
public class Log {

    private Plugin plugin;
    @Setter
    private boolean isLogEnabled;

    public void severe(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + plugin.getName()  + " ERROR] " + message);
    }

    public void fine(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("[" + plugin.getName()  + " SUCCESS] " + message);
    }

    public void info(String message) {
        if (isLogEnabled) Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + " DEBUG] " + message);
    }

    public void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + " ALERT] " + message);
    }

    public void loading(String message) {
        Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + " " + message);
    }

}
