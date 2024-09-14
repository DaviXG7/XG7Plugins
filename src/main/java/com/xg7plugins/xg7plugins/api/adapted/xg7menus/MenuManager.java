package com.xg7plugins.xg7plugins.api.adapted.xg7menus;

import com.xg7plugins.xg7plugins.api.adapted.xg7menus.player.PlayerMenu;
import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager {

    private final Plugin plugin;

    private final Map<UUID, PlayerMenu> playerMenuMap = new HashMap<>();

    public MenuManager(Plugin plugin) {
        this.plugin = plugin;
    }

}
