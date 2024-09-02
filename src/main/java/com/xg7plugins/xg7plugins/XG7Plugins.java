package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.CommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class XG7Plugins extends JavaPlugin {
    @Getter
    private static final List<Plugin> plugins = new ArrayList<>();
    @Getter
    private static int version;
    @Getter
    private static final CommandManager commandManager = new CommandManager();

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void registerPlugin(Plugin plugin) {
        plugins.add(plugin);

        CommandManager.registerCommands(plugin);
    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }
}
