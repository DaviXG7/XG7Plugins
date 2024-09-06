package com.xg7plugins.xg7plugins.data.config;

import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class Configs {

    private final HashMap<String, Config> configs = new HashMap<>();

    public void register(Plugin plugin) {
        plugin.getConfigs().forEach(config -> configs.put(plugin.getName() + ":" + config.getName(), config));
    }

    public Config getConfig(Plugin plugin, String name) {
        return configs.get(plugin.getName() + ":" + name);
    }
    public void unregister(Plugin plugin) {
        configs.keySet().stream().filter(key -> key.startsWith(plugin.getName())).forEach(configs::remove);
    }


}
