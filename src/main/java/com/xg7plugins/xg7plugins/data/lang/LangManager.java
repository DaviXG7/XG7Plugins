package com.xg7plugins.xg7plugins.data.lang;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.xg7plugins.data.database.Query;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import com.xg7plugins.xg7plugins.utils.reflection.PlayerNMS;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Getter
public class LangManager {

    private final Plugin plugin;
    private final Cache<String, YamlConfiguration> langs;
    private final Cache<UUID, LangEntity> players;
    private final String mainLang;
    private final String[] defLangs;

    public LangManager(Plugin plugin, String[] defaultLangs) {
         this.plugin = plugin;
         this.defLangs = defaultLangs;

        Config config = plugin.getConfigsManager().getConfig("config");

        this.mainLang = config.get("main-lang");
        this.langs = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("lang-cache-expires")), TimeUnit.MILLISECONDS).build();
        this.players = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("lang-cache-expires")), TimeUnit.MILLISECONDS).build();

        loadAllLangs();
    }

    @SneakyThrows
    public void loadAllLangs() {
        File dir = new File(plugin.getDataFolder(), "langs");
        if (!dir.exists()) dir.mkdirs();
        if (dir.listFiles() != null && Objects.requireNonNull(dir.listFiles()).length != 0) {
            Arrays.stream(dir.listFiles()).forEach(file -> langs.put(file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file)));
        }
        for (String lang : defLangs) {
            File file = new File(dir, lang + ".yml");
            if (!file.exists()) plugin.saveResource("langs/" + lang + ".yml", false);
            langs.put(lang, YamlConfiguration.loadConfiguration(file));
        }
    }

    public YamlConfiguration getLang(String lang) {
        if (langs.asMap().containsKey(lang)) return langs.getIfPresent(lang);

        File file = new File(plugin.getDataFolder(), "langs/" + lang + ".yml");
        if (!file.exists()) plugin.saveResource("langs/" + lang + ".yml", false);

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        langs.put(lang, configuration);

        return configuration;
    }
    public void updatePlayer(Player player, String lang) {
        this.players.put(player.getUniqueId(),new LangEntity(player.getUniqueId(),lang));
    }

    public String getPath(Player player, String path) {
        return getLangByPlayer(player.getUniqueId(), XG7Plugins.getMinecraftVersion() >= 12 ? player.getLocale() : PlayerNMS.cast(player).getCraftPlayerHandle().getField("locale")).getString(path);
    }

    @SneakyThrows
    public YamlConfiguration getLangByPlayer(UUID id, String playerLocale) {

        if (id == null) return getLang(mainLang);
        if (players.asMap().containsKey(id)) return getLang(players.asMap().get(id).getLangId());

        return Query.create(XG7Plugins.getInstance(),"SELECT * FROM LangEntity WHERE playerUUID = ?", id)
                .thenApplyAsync(q -> {

                            if (!q.hasNextLine()) {

                                Config config = plugin.getConfigsManager().getConfig("config");

                                if (config.get("auto-chose-lang")) {
                                    for (String locale : langs.asMap().keySet()) {
                                        if (playerLocale.equals(locale)) {
                                            LangEntity newLang = new LangEntity(id,locale);

                                            players.put(id, newLang);
                                            EntityProcessor.insetEntity(XG7Plugins.getInstance(), newLang);

                                            return getLang(newLang.getLangId());
                                        }
                                    }

                                }

                                LangEntity newLang = new LangEntity(id,mainLang);
                                EntityProcessor.insetEntity(XG7Plugins.getInstance(), newLang);

                                players.put(id, newLang);
                                return getLang(newLang.getLangId());
                            }

                            LangEntity entity = q.get(LangEntity.class);
                            players.put(id, entity);

                            return getLang(entity.getLangId());

                        }
                ).get();
    }
}
