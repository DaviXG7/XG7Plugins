package com.xg7plugins.xg7plugins.api.adapted.xg7menus.player;

import com.xg7plugins.xg7plugins.api.adapted.xg7menus.MenuPermissions;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.BaseMenu;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.events.ClickEvent;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.events.MenuEvent;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.boot.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class PlayerMenu extends BaseMenu {

    private final Map<Integer, ItemStack> oldItems = new HashMap<>();

    public PlayerMenu(Consumer<ClickEvent> defaultClickEvent, Consumer<MenuEvent> openEvent, Consumer<MenuEvent> closeEvent, Map<Integer, ItemStack> items, Map<Integer, Consumer<ClickEvent>> clickEvents, EnumSet<MenuPermissions> permissions, Player player, Plugin plugin) {
        super(defaultClickEvent, openEvent, closeEvent, items, clickEvents,permissions,player,plugin);
    }
    public void give() {
        IntStream.range(0, player.getInventory().getSize()).filter(i -> player.getInventory().getItem(i) != null).forEach(i -> oldItems.put(i, player.getInventory().getItem(i)));
        player.getInventory().clear();
        items.forEach((key, value) -> player.getInventory().setItem(key, value));
        plugin.getMenuManager().getPlayerMenuMap().put(player.getUniqueId(),this);
    }
    public void clear() {
        player.getInventory().clear();
        oldItems.forEach((key, value) -> player.getInventory().setItem(key, value));
        oldItems.clear();
        plugin.getMenuManager().getPlayerMenuMap().remove(player.getUniqueId());
    }

    public void setItem(int slot, ItemStack item) {
        items.put(slot, item);
    }
    public void setItem(int slot, ItemStack item, Consumer<ClickEvent> clickEvent) {
        items.put(slot, item);
        clickEvents.put(slot, clickEvent);
    }
    public void setClickEvent(int slot, Consumer<ClickEvent> clickEvent) {
        clickEvents.put(slot, clickEvent);
    }

    public void update() {
        items.forEach((key, value) -> player.getInventory().setItem(key, value));
    }

    public void updateItem(int slot, ItemBuilder builder) {
        player.getInventory().setItem(slot, builder.toItemStack());
        items.put(slot, builder.toItemStack());
        if (builder.getEvent() != null) clickEvents.put(slot, builder.getEvent());
        else clickEvents.remove(slot);
    }
}
