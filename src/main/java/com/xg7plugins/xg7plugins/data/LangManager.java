package com.xg7plugins.xg7plugins.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.xg7plugins.data.database.Query;
import com.xg7plugins.xg7plugins.data.database.mainData.LangEntity;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LangManager {

    private Plugin plugin;
    private Cache<String, YamlConfiguration> langs;
    private Cache<UUID, LangEntity> players;
    private String mainLang;

    public LangManager(Plugin plugin) {

        this.plugin = plugin;

        Config config = plugin.getConfigsManager().getConfig("config");

        this.langs = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("lang-cache-expires")), TimeUnit.MILLISECONDS).build();
        this.players = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("lang-cache-expires")), TimeUnit.MILLISECONDS).build();

        File dir = new File(plugin.getDataFolder(), "langs");
        if (!dir.exists()) dir.mkdirs();

        mainLang = config.get("main-lang");

        File file = new File(dir, mainLang);
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

    @SneakyThrows
    public LangEntity getLangByPlayer(Player player) {

        if (players.asMap().containsKey(player.getUniqueId())) return players.asMap().get(player.getUniqueId());

        return Query.create(plugin,"SELECT * FROM langentity WHERE playeruuid = ?", player.getUniqueId())
                .thenApplyAsync(q -> {

                            if (!q.hasNextLine()) {

                                Config config = plugin.getConfigsManager().getConfig("config");

                                if (config.get("auto-chose-lang")) {

                                    for (YamlConfiguration cfg : langs.asMap().values()) {
                                        String locale = cfg.getString("lang-locale");
                                        if (player.getLocale().equals(locale)) {
                                            LangEntity newLang = new LangEntity(cfg.getName(), player.getUniqueId());

                                            players.put(player.getUniqueId(), newLang);

                                            EntityProcessor.insetEntity(plugin, newLang);

                                            return newLang;
                                        }
                                    }

                                }

                                LangEntity newLang = new LangEntity(mainLang, player.getUniqueId());

                                EntityProcessor.insetEntity(plugin, newLang);

                                players.put(player.getUniqueId(), newLang);
                                return newLang;
                            }

                            LangEntity entity = q.get(LangEntity.class);
                            players.put(player.getUniqueId(), entity);

                            return entity;

                        }
                ).get();
    }
}
