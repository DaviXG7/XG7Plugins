package com.xg7plugins.xg7plugins.boot;

import com.xg7plugins.xg7plugins.commands.CommandManager;
import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.config.Configs;
import com.xg7plugins.xg7plugins.data.database.DBManager;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionClass;
import lombok.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Plugin extends JavaPlugin {

    private final String prefix;
    private final String name;

    private final Configs configsManager = new Configs(this);
    private final CommandManager commandManager = new CommandManager(this);

    private String customPrefix = prefix;
    private boolean logEnabled = false;
    private List<String> enabledWorlds = Collections.emptyList();

    @Override
    public abstract void onEnable();
    @Override
    public abstract void onDisable();
    @Override
    public abstract void onLoad();

    public abstract List<ICommand> getCommands();
    public abstract List<Config> getConfigs();

}
