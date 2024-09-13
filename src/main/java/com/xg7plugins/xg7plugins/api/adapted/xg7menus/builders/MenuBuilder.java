package com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders;

import com.xg7plugins.xg7plugins.api.adapted.xg7menus.BaseItemBuilder;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.BaseMenuBuilder;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.MenuException;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.gui.Menu;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.item.SkullItemBuilder;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.stream.Collectors;

public class MenuBuilder extends BaseMenuBuilder<MenuBuilder> {

    protected String title;
    protected int size;
    protected InventoryType type;

    public MenuBuilder title(String title) {
        this.title = title;
        return this;
    }
    public MenuBuilder size(int size) {
        this.size = size;
        return this;
    }
    public MenuBuilder rows(int rows) {
        this.size = rows * 9;
        return this;
    }
    public MenuBuilder type(InventoryType type) {
        this.type = type;
        return this;
    }


    @Override
    public Menu build(Player player) {
        if (title == null) throw new MenuException("The inventory must have a title!");

        Map<Integer, ItemStack> buildItems = items.entrySet().stream()
                .collect(
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

                        )
        );

        Menu menu = type == null ? new Menu(Text.format(title,null).setPlaceholders(player).getText(), size, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player) : new Menu(Text.format(title).setPlaceholders(player).getText(), type, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player);
        return menu;
    }
}
