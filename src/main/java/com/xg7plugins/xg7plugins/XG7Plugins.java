package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.CommandManager;
import com.xg7plugins.xg7plugins.data.config.Configs;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class XG7Plugins extends JavaPlugin {
    @Getter
    private static final HashMap<String, Plugin> plugins = new HashMap<>();
    @Getter
    private static int version;
    @Getter
    private static final CommandManager commandManager = new CommandManager();
    @Getter
    private static XG7Plugin defaultPlugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerPlugin(new XG7Plugin());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void registerPlugin(Plugin plugin) {
        plugins.put(plugin.getName(), plugin);

        Configs.register(plugin);

        CommandManager.registerCommands(plugin);

    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Plugin> T getPlugin(String name) {
        return (T) plugins.get(name);
    }
}
