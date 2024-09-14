package com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.item;

import com.xg7plugins.xg7plugins.api.adapted.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.xg7plugins.api.adapted.xg7menus.XSeries.XMaterial;
import com.xg7plugins.xg7plugins.boot.Plugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

    public ItemBuilder(ItemStack stack) {
        super(stack);
    }

    @Contract("_ -> new")
    public static @NotNull ItemBuilder from(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }
    @Contract("_ -> new")
    public static @NotNull ItemBuilder from(@NotNull MaterialData material) {
        return new ItemBuilder(material.toItemStack());
    }
    @Contract("_ -> new")
    public static @NotNull ItemBuilder from(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }
    public static <B extends BaseItemBuilder<B>> B from(@NotNull String material) {

        if (material.startsWith("eyJ0")) return (B) new SkullItemBuilder().setValue(material);
        if (material.equals("THIS_PLAYER")) return (B) new SkullItemBuilder().renderSkullPlayer();
        if (material.split(", ").length == 2) {
            String[] args = material.split(", ");
            return (B) from(new MaterialData(XMaterial.valueOf(args[0]).parseMaterial(), Byte.parseByte(args[0])));
        }

        return (B) from(XMaterial.valueOf(material).parseItem());
    }

}
