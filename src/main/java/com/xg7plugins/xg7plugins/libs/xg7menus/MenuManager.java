package com.xg7plugins.xg7plugins.libs.xg7menus;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.BaseMenu;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class MenuManager {

    private final XG7Plugins plugin;

    private final Map<UUID, PlayerMenu> playerMenuMap = new HashMap<>();
    private final Cache<String, BaseMenu> cachedMenus;

    public MenuManager(XG7Plugins plugin) {
        this.plugin = plugin;
        this.cachedMenus = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, plugin.getConfigsManager().getConfig("config").get("menu-cache-expires")), TimeUnit.MILLISECONDS).build();
    }

}
