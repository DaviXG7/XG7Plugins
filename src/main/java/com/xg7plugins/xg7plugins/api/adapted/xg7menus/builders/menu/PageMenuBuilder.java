package com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.menu;

import com.xg7plugins.xg7plugins.api.adapted.xg7menus.MenuException;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.Slot;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.BaseMenuBuilder;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.gui.ItemsPageMenu;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.item.SkullItemBuilder;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageMenuBuilder extends BaseMenuBuilder<PageMenuBuilder> {


    protected String title;
    protected int size;
    protected InventoryType type;

    private List<ItemBuilder> pageItems = new ArrayList<>();
    private Slot initSlot;
    private Slot finalSlot;
    private boolean keepSavingPageIndex = false;

    public PageMenuBuilder title(String title) {
        this.title = title;
        return this;
    }
    public PageMenuBuilder size(int size) {
        this.size = size;
        return this;
    }
    public PageMenuBuilder rows(int rows) {
        this.size = rows * 9;
        return this;
    }
    public PageMenuBuilder type(InventoryType type) {
        this.type = type;
        return this;
    }
    public PageMenuBuilder items(ItemBuilder... items) {
        this.pageItems.addAll(Arrays.asList(items));
        return this;
    }
    public PageMenuBuilder items(ItemStack... items) {
        this.pageItems.addAll(Arrays.stream(items).map(ItemBuilder::from).collect(Collectors.toList()));
        return this;
    }
    public PageMenuBuilder setItemStacks(@NotNull List<ItemStack> items) {
        this.pageItems = items.stream().map(ItemBuilder::from).collect(Collectors.toList());
        return this;
    }
    public PageMenuBuilder setItems(List<ItemBuilder> items) {
        this.pageItems = items;
        return this;
    }
    public PageMenuBuilder setInitStorageSlot(Slot initSlot) {
        this.initSlot = initSlot;
        return this;
    }
    public PageMenuBuilder setFinalStorageSlot(Slot finalSlot) {
        this.finalSlot = finalSlot;
        return this;
    }
    public PageMenuBuilder setArea(Slot initSlot, Slot finalSlot) {
        this.initSlot = initSlot;
        this.finalSlot = finalSlot;
        return this;
    }
    public PageMenuBuilder keepSavingPageIndex(boolean keepSavingPageIndex) {
        this.keepSavingPageIndex = keepSavingPageIndex;
        return this;
    }




    @Override
    public ItemsPageMenu build(Player player) {
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

        ItemsPageMenu menu = type == null ? new ItemsPageMenu(Text.format(title).getWithPlaceholders(player), size, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player,initSlot,finalSlot,pageItems,keepSavingPageIndex) : new ItemsPageMenu(Text.format(title).getWithPlaceholders(player), type, buildItems, clickEventMap, defaultClickEvent, openMenuEvent, closeMenuEvent, allowedPermissions, player,initSlot,finalSlot,pageItems,keepSavingPageIndex);
        return menu;
    }
}
