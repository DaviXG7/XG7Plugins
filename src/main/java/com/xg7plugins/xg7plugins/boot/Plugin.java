package com.xg7plugins.xg7plugins.boot;

import com.xg7plugins.xg7plugins.commands.CommandManager;
import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.config.Configs;
import com.xg7plugins.xg7plugins.events.Event;
import lombok.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public abstract class Plugin extends JavaPlugin {

    private String prefix;

    private final Configs configsManager;
    private final CommandManager commandManager;

    private String customPrefix = prefix;
    private boolean logEnabled = false;
    private List<String> enabledWorlds = Collections.emptyList();

    public Plugin(String prefix) {
        this.prefix = prefix;
        this.commandManager = new CommandManager(this);
        this.configsManager = new Configs(this);
    }

    @Override
    public abstract void onEnable();
    @Override
    public abstract void onDisable();
    @Override
    public abstract void onLoad();

    public abstract List<ICommand> getCommands();
    public abstract List<Config> getConfigs();
    public abstract List<Event> getEvents();
    public abstract List<Event> getPacketEvents();

}
