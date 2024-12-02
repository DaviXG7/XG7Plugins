package com.xg7plugins.libs.newxg7menus.menus.holders;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.events.ClickEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

@Getter
public class ConfirmationMenuHolder extends MenuHolder {

    public ConfirmationMenuHolder(String id, Plugin plugin, Inventory inventory, Player player) {
        super(id, plugin, inventory, player);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
