package com.xg7plugins.lang;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Getter
public class LangManager {

    private final XG7Plugins plugin;
    private final ObjectCache<String, Config> langs;
    private final String mainLang;
    private final String[] defLangs;
    private final boolean langEnabled;

    public LangManager(XG7Plugins plugin, String[] defaultLangs) {
        this.plugin = plugin;
        this.defLangs = defaultLangs;
        this.langEnabled = Config.mainConfigOf(plugin).get("lang-enabled", Boolean.class).orElse(true);

        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");

        this.mainLang = config.get("main-lang", String.class).orElse("en");
        this.langs = new ObjectCache<>(
                plugin,
                config.getTime("lang-cache-expires").orElse(60 * 10 * 1000L),
                false,
                "langs",
                true,
                String.class,
                Config.class
        );

        plugin.getDebug().loading("Loaded!");
    }

    public CompletableFuture<Void> loadLangsFrom(Plugin plugin) {
        return CompletableFuture.runAsync(() -> {

            if (!langEnabled) {
                loadLang(plugin, mainLang).join();
                return;
            }

            for (String lang : defLangs) {
                loadLang(plugin, lang).join();
            }
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));
    }

    public CompletableFuture<Void> loadLang(Plugin plugin, String lang) {

        return CompletableFuture.runAsync(() -> {

            if (langs.containsKey(plugin.getName() + ":" + lang).join()) return;

            File langFolder = new File(plugin.getDataFolder(), "langs");
            if (!langFolder.exists()) langFolder.mkdirs();

            File langFile = new File(langFolder, lang + ".yml");
            if (!langFile.exists()) plugin.saveResource("langs/" + lang + ".yml", false);
            langs.put(plugin.getName() + ":" + lang, Config.of("langs/" + lang, plugin));
        }, XG7Plugins.taskManager().getAsyncExecutors().get("files"));
    }

    public CompletableFuture<Lang> getLang(Plugin plugin, String lang, boolean selected) {
        return CompletableFuture.supplyAsync(() -> {

            String finalLang = lang;

            if (!langEnabled) finalLang = mainLang;

            if (langs.containsKey(plugin.getName() + ":" + finalLang).join()) {
                return new Lang(plugin, langs.get(plugin.getName() + ":" + finalLang).join(), finalLang,selected);
            }

            loadLang(plugin, finalLang).join();

            return new Lang(plugin, langs.get(plugin.getName() + ":" + finalLang).join(), finalLang, selected);

        });
    }

    public CompletableFuture<Lang> getLang(Plugin plugin, String lang) {
        return getLang(plugin, lang, false);
    }

    public CompletableFuture<Lang> getLangByPlayer(Plugin plugin, Player player) {
        if (!langEnabled || player == null) return getLang(plugin, mainLang);


        return CompletableFuture.supplyAsync(() -> {
            PlayerData playerData = XG7Plugins.getInstance().getPlayerDataDAO().get(player.getUniqueId()).join();

            if (playerData == null || playerData.getLangId() == null) return getLang(plugin, mainLang,true).join();

            return getLang(plugin, playerData.getLangId(), true).join();
        });
    }

    public CompletableFuture<String> getNewLangFor(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null!");

        if (!langEnabled) return CompletableFuture.completedFuture(mainLang);

        return CompletableFuture.supplyAsync(() -> {

            if(!Config.mainConfigOf(plugin).get("auto-chose-lang", Boolean.class).orElse(true)) return mainLang;

            loadLangsFrom(XG7Plugins.getInstance()).join();

            List<Config> langs = this.langs.asMap().join().entrySet().stream().filter(e -> e.getKey().startsWith(XG7Plugins.getInstance().getName() + ":")).map(Map.Entry::getValue).collect(Collectors.toList());

            String locale = MinecraftVersion.isNewerOrEqual(12) ? player.getLocale() : ReflectionObject.of(player).getMethod("getHandle").invokeToRObject().getField("locale");

            return langs.stream().filter(lang -> lang.get("locale", String.class).orElse("en_US").equals(locale)).findFirst().map(config -> config.getName().replace("langs/", "")).orElse(mainLang);
        });

    }




}
