package com.xg7plugins.xg7plugins.libs.xg7menus;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager {

    private final XG7Plugins plugin;

    private final Map<UUID, PlayerMenu> playerMenuMap = new HashMap<>();

    public MenuManager(XG7Plugins plugin) {
        this.plugin = plugin;
    }

}
