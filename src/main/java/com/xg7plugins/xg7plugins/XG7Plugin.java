package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XG7Plugin implements Plugin {

    @Override
    public String getPrefix() {
        return "[XG7Plugins] ";
    }

    @Override
    public boolean log() {
        return false;
    }

    @Override
    public String getName() {
        return "XG7Plugins";
    }

    @Override
    public JavaPlugin getPlugin() {
        return XG7Plugins.getInstance();
    }

    @Override
    public List<ICommand> getCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<Config> getConfigs() {
        return Arrays.asList(new Config(this, "config"));
    }

    @Override
    public boolean isDBEnabled() {
        return true;
    }

    @Override
    public List<String> getEnabledWorlds() {
        return Plugin.super.getEnabledWorlds();
    }
}
