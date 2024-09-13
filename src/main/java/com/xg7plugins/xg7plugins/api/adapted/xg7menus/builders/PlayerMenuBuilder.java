package com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders;

import com.xg7plugins.xg7menus.api.menus.builders.BaseItemBuilder;
import com.xg7plugins.xg7menus.api.menus.builders.BaseMenuBuilder;
import com.xg7plugins.xg7menus.api.menus.builders.item.SkullItemBuilder;
import com.xg7plugins.xg7menus.api.menus.player.PlayerMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.stream.Collectors;

public class PlayerMenuBuilder extends BaseMenuBuilder<PlayerMenuBuilder> {
    @Override
    public PlayerMenu build(Player player) {
        return new PlayerMenu(defaultClickEvent,openMenuEvent,closeMenuEvent,items.entrySet().stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> {
                                    BaseItemBuilder<?> builder = entry.getValue();
                                    if (builder instanceof SkullItemBuilder) {
                                        SkullMeta meta = (SkullMeta) builder.toItemStack().getItemMeta();
                                        if ("THIS_PLAYER".equals(meta.getOwner())) return ((SkullItemBuilder) builder).setOwner(player.getName()).setPlaceHolders(player).toItemStack();
                                    }
                                    return builder.setPlaceHolders(player).toItemStack();
                                }

                        )), clickEventMap, allowedPermissions, player);
    }
}
