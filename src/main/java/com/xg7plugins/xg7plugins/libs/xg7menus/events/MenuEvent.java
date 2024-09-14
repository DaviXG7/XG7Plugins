package com.xg7plugins.xg7plugins.libs.xg7menus.events;

import com.xg7plugins.xg7plugins.libs.xg7menus.builders.BaseMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class MenuEvent {
    BaseMenu menu;
    Player player;
}
