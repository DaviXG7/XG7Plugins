package com.xg7plugins.libs.newxg7menus.menus.gui;

import com.xg7plugins.Plugin;
import com.xg7plugins.libs.newxg7menus.Slot;
import com.xg7plugins.libs.newxg7menus.item.Item;
import com.xg7plugins.libs.newxg7menus.menus.holders.MenuHolder;
import com.xg7plugins.libs.newxg7menus.menus.holders.PageMenuHolder;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class PageMenu extends Menu {

    private final Slot startEdge;
    private final Slot endEdge;
    private final List<Item> itemList;

    public PageMenu(Plugin plugin, String id, String title, int size, Slot pos1, Slot pos2, List<Item> itemList) {
        super(plugin, id, title, size);

        int startX = Math.min(pos1.getColumn(), pos2.getColumn());
        int finalX = Math.max(pos1.getColumn(), pos2.getColumn());
        int startY = Math.min(pos1.getRow(), pos2.getRow());
        int finalY = Math.min(pos1.getRow(), pos2.getRow());

        this.startEdge = new Slot(startX, startY);
        this.endEdge = new Slot(finalX, finalY);
        this.itemList = itemList;

    }

    public PageMenu(Plugin plugin, String id, String title, InventoryType type, Slot pos1, Slot pos2, List<Item> itemList) {
        super(plugin, id, title, type);

        int startX = Math.min(pos1.getColumn(), pos2.getColumn());
        int finalX = Math.max(pos1.getColumn(), pos2.getColumn());
        int startY = Math.min(pos1.getRow(), pos2.getRow());
        int finalY = Math.min(pos1.getRow(), pos2.getRow());

        this.startEdge = new Slot(startX, startY);
        this.endEdge = new Slot(finalX, finalY);
        this.itemList = itemList;
    }




    @Override
    public void open(Player player) {

        PageMenuHolder holder = new PageMenuHolder(id, plugin, type == null ? Bukkit.createInventory(player, size, Text.getWithPlaceholders(plugin, title, player)) : Bukkit.createInventory(player, type, Text.getWithPlaceholders(plugin, title, player)), player, itemList.size() / ((endEdge.getRow() - startEdge.getRow()) * (endEdge.getColumn() - startEdge.getColumn())));
        player.openInventory(holder.getInventory());
        putItems(player, holder.getInventory());

    }


}
