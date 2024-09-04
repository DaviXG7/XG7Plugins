package com.xg7plugins.xg7plugins.data.config;

import com.xg7plugins.xg7plugins.Plugin;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class Configs {

    private static final HashMap<String, Config> configs = new HashMap<>();

    public static void register(Plugin plugin) {
        plugin.getConfigs().forEach(config -> Configs.configs.put(plugin.getName() + ":" + config.getName(), config));
    }

    public static Config getConfig(Plugin plugin, String name) {
        return Configs.configs.get(plugin.getName() + ":" + name);
    }
    public static void unregister(Plugin plugin) {
        Configs.configs.keySet().stream().filter(key -> key.startsWith(plugin.getName())).forEach(configs::remove);
    }


}
