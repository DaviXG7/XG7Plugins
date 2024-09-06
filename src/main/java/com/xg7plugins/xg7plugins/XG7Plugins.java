package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.CommandManager;
import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.config.Configs;
import com.xg7plugins.xg7plugins.data.database.DBManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
public final class XG7Plugins extends Plugin {

    private static XG7Plugins plugin;

    private final HashMap<String, Plugin> plugins = new HashMap<>();

    private final CommandManager commandManager = new CommandManager();
    private final Configs configsManager = new Configs();
    private final DBManager databaseManager = new DBManager(this);

    private XG7Plugins() {
        super("[XG7Plugins]", "XG7Plugins");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onLoad() {

    }

    @Override
    public List<ICommand> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public List<Config> getConfigs() {
        return Arrays.asList(new Config(this, "config"));
    }

    public void registerPlugin(Plugin plugin) {
        plugins.put(plugin.getName(), plugin);
        configsManager.register(plugin);
        commandManager.registerCommands(plugin);
        databaseManager.connectPlugin(plugin);
    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Plugin> T getPlugin(String name) {
        return (T) plugins.get(name);
    }
}
