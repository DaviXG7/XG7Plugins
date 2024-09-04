package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public interface Plugin {

    String getPrefix();
    boolean log();
    String getName();

    JavaPlugin getPlugin();

    List<ICommand> getCommands();
    List<Config> getConfigs();

    boolean isDBEnabled();

    default List<String> getEnabledWorlds() {
        return Collections.singletonList("all");
    }

}
