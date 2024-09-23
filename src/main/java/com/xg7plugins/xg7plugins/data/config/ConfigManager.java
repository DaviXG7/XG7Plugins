package com.xg7plugins.xg7plugins.data.config;

import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class ConfigManager {

    private final HashMap<String, Config> configs = new HashMap<>();

    public ConfigManager(Plugin plugin) {
        plugin.getConfigs().forEach(config -> configs.put(config.getName(), config));
    }

    public Config getConfig(String name) {
        return configs.get(name);
    }


}
