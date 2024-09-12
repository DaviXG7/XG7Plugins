package com.xg7plugins.xg7plugins.boot;

import com.xg7plugins.xg7plugins.commands.CommandManager;
import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.config.Configs;
import com.xg7plugins.xg7plugins.data.lang.LangManager;
import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.utils.Log;
import lombok.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public abstract class Plugin extends JavaPlugin {

    private String prefix;

    private final Configs configsManager;
    private final CommandManager commandManager;
    private final LangManager langManager;
    private final Log log;

    private String customPrefix = prefix;
    private List<String> enabledWorlds = Collections.emptyList();

    public Plugin(String prefix) {
        this.prefix = prefix;
        this.commandManager = new CommandManager(this);
        this.configsManager = new Configs(this);
        this.langManager = new LangManager(this);
        this.log = new Log(this, false);
    }

    public void sendMessage(CommandSender sender, String path) {}

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
