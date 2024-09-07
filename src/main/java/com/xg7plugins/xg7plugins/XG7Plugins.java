package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.database.DBManager;
import com.xg7plugins.xg7plugins.data.database.Query;
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

    private DBManager databaseManager;

    private final HashMap<String, Plugin> plugins = new HashMap<>();

    private XG7Plugins() {
        super("[XG7Plugins]", "XG7Plugins");
    }

    @Override
    public void onEnable() {
        this.databaseManager = new DBManager(this);
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

    public static void register(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName(), plugin);
        xg7Plugins.getDatabaseManager().connectPlugin(plugin);
    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Plugin> T getPlugin(String name) {
        return (T) plugins.get(name);
    }
}
