package com.xg7plugins.modules.xg7menus.menus.gui;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.PageMenuHolder;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.TextCentralizer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

@Getter
public abstract class PageMenu extends Menu {

    private final Slot startEdge;
    private final Slot endEdge;

    public PageMenu(Plugin plugin, String id, String title, int size, Slot pos1, Slot pos2) {
        super(plugin, id, title, size);

        int startRow = Math.min(pos1.getRow(), pos2.getRow());
        int finalRow = Math.max(pos1.getRow(), pos2.getRow());
        int startColumn = Math.min(pos1.getColumn(), pos2.getColumn());
        int finalColumn = Math.max(pos1.getColumn(), pos2.getColumn());

        this.startEdge = new Slot(startRow, startColumn);
        this.endEdge = new Slot(finalRow, finalColumn);

    }

    public PageMenu(Plugin plugin, String id, String title, InventoryType type, Slot pos1, Slot pos2) {
        super(plugin, id, title, type);

        int startRow = Math.min(pos1.getRow(), pos2.getRow());
        int finalRow = Math.max(pos1.getRow(), pos2.getRow());
        int startColumn = Math.min(pos1.getColumn(), pos2.getColumn());
        int finalColumn = Math.max(pos1.getColumn(), pos2.getColumn());

        this.startEdge = new Slot(startRow, startColumn);
        this.endEdge = new Slot(finalRow, finalColumn);
    }

    public abstract List<Item> pagedItems(Player player);


    @Override
    public void open(Player player) {

        PageMenuHolder holder = new PageMenuHolder(id, plugin, Text.detectLangs(player, plugin,title).join().getPlainText(), size, type, this, player);
        player.openInventory(holder.getInventory());
        putItems(player, holder);
        holder.goPage(0);

    }


}
