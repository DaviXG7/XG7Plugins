package com.xg7plugins.xg7plugins.data.lang;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LangManager {

    private Plugin plugin;
    private Cache<String, YamlConfiguration> langs;

    public LangManager(Plugin plugin) {

        this.plugin = plugin;

        Config config = plugin.getConfigsManager().getConfig("config");

        this.langs = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("lang-cache-expires")), TimeUnit.MILLISECONDS).build();

        File dir = new File(plugin.getDataFolder(), "langs");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "langs/" + config.get("main-lang"));
        if (!file.exists()) plugin.saveResource(file.getName(), false);

        langs.put(file.getName(), YamlConfiguration.loadConfiguration(file));

    }

    public void
}
