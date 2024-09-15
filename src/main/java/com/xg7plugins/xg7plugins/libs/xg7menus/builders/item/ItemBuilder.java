package com.xg7plugins.xg7plugins.libs.xg7menus.builders.item;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

    public ItemBuilder(ItemStack stack, Plugin plugin) {
        super(stack,plugin);
    }

    @Contract("_ -> new")
    public static @NotNull ItemBuilder from(Material material,Plugin plugin) {
        return new ItemBuilder(new ItemStack(material),plugin);
    }
    @Contract("_ -> new")
    public static @NotNull ItemBuilder from(@NotNull MaterialData material,Plugin plugin) {
        return new ItemBuilder(material.toItemStack(),plugin);
    }
    @Contract("_ -> new")
    public static @NotNull ItemBuilder from(ItemStack itemStack,Plugin plugin) {
        return new ItemBuilder(itemStack,plugin);
    }
    public static <B extends BaseItemBuilder<B>> B from(@NotNull String material, Plugin plugin) {

        if (material.startsWith("eyJ0")) return (B) new SkullItemBuilder(plugin).setValue(material);
        if (material.equals("THIS_PLAYER")) return (B) new SkullItemBuilder(plugin).renderSkullPlayer();
        if (material.split(", ").length == 2) {
            String[] args = material.split(", ");
            return (B) from(new MaterialData(XMaterial.valueOf(args[0]).parseMaterial(), Byte.parseByte(args[0])),plugin);
        }

        return (B) from(XMaterial.valueOf(material).parseItem(),plugin);
    }

}
