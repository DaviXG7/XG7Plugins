package com.xg7plugins.xg7plugins.data.lang;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.xg7plugins.data.database.mainData.LangEntity;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
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

        File file = new File(dir, config.get("main-lang"));
        Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(lang -> !lang.exists()).forEach(lang -> plugin.saveResource(lang.getName(), false));


        langs.put(file.getName(), YamlConfiguration.loadConfiguration(file));

        EntityProcessor.createTableOf(plugin, LangEntity.class);

    }

    public YamlConfiguration getLang(String lang) {
        if (langs.asMap().containsKey(lang)) return langs.getIfPresent(lang);

        File file = new File(plugin.getDataFolder(), "langs/" + lang + ".yml");
        if (!file.exists()) plugin.saveResource("langs/" + lang + ".yml", false);

        return YamlConfiguration.loadConfiguration(file);
    }

    public void loadPlayer(Player player) {
        Locale playerLang = Locale.forLanguageTag(player.getLocale());


    }
}
