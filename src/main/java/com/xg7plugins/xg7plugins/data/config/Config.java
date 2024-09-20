package com.xg7plugins.xg7plugins.data.config;

import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class Config {

    private final Plugin plugin;
    private final String name;
    private YamlConfiguration config;

    @SneakyThrows
    public Config(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        File configFile = new File(plugin.getDataFolder(), name + ".yml");

        if (!configFile.exists()) plugin.saveResource(name + ".yml", false);

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public <T> T get(String path) {
        return (T) config.get(path);
    }
    public ConfigurationSection getConfigutationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public void set(String path, Object value) {
        config.set(path,value);
    }

    @SneakyThrows
    public void save() {
        config.save(new File(plugin.getDataFolder(), name + ".yml"));
    }

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), name + ".yml");
        if (!configFile.exists()) plugin.saveResource(name + ".yml", false);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getConfigsManager().getConfigs().put(name,this);
    }

}
