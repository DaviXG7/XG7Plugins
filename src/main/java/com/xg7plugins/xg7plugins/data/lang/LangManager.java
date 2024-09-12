package com.xg7plugins.xg7plugins.data.lang;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.xg7plugins.data.database.Query;
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

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        langs.put(lang, configuration);

        return configuration;
    }

    public String getPath(Player player, String path) {
        return getLangByPlayer(player.getUniqueId(), player.getLocale()).getString(path);
    }

    @SneakyThrows
    public YamlConfiguration getLangByPlayer(UUID id, String playerLocale) {

        if (id == null) return getLang(mainLang);

        if (players.asMap().containsKey(id)) return getLang(players.asMap().get(id).getLangId());


        return (YamlConfiguration) Query.create(plugin,"SELECT * FROM langentity WHERE playeruuid = ?", id)
                .thenApplyAsync(q -> {

                            if (!q.hasNextLine()) {

                                Config config = plugin.getConfigsManager().getConfig("config");

                                if (config.get("auto-chose-lang")) {

                                    for (YamlConfiguration cfg : langs.asMap().values()) {
                                        String locale = cfg.getString("lang-locale");
                                        if (playerLocale.equals(locale)) {
                                            LangEntity newLang = new LangEntity(cfg.getName(), id);

                                            players.put(id, newLang);

                                            EntityProcessor.insetEntity(plugin, newLang);

                                            return getLang(newLang.getLangId());
                                        }
                                    }

                                }

                                LangEntity newLang = new LangEntity(mainLang, id);

                                EntityProcessor.insetEntity(plugin, newLang);

                                players.put(id, newLang);
                                return newLang;
                            }

                            LangEntity entity = q.get(LangEntity.class);
                            players.put(id, entity);

                            return getLang(entity.getLangId());

                        }
                ).get();
    }
}
