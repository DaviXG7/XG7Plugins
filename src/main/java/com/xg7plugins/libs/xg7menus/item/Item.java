package com.xg7plugins.libs.xg7menus.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.utils.reflection.nms.NMSUtil;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Item {

    protected ItemStack itemStack;
    protected int slot;

    private static final ReflectionClass nbtTagCompoundClass = NMSUtil.getNMSClassViaVersion(17 , "NBTTagCompound", "nbt.NBTTagCompound");
    private static final ReflectionClass nmsItemStackClass = NMSUtil.getNMSClassViaVersion(17, "ItemStack", "world.item.ItemStack");
    private static final ReflectionClass craftItemStackClass = NMSUtil.getCraftBukkitClass("inventory.CraftItemStack");


    protected HashMap<String, String> buildPlaceholders = new HashMap<>();


    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.slot = -1;
    }

    public static Item from(ItemStack itemStack) {
        return new Item(itemStack);
    }
    public static Item from(XMaterial material) {
        return new Item(material.parseItem());
    }
    public static Item from(String material) {
        if (material == null) return Item.from(Material.STONE);
        if (material.startsWith("eyJ0")) return SkullItem.newSkull().setValue(material);
        if (material.equals("THIS_PLAYER")) return SkullItem.newSkull().renderSkullPlayer(true);
        if (material.split(", ").length == 2) {
            String[] args = material.split(", ");
            return Item.from(new MaterialData(Material.getMaterial(args[0]), Byte.parseByte(args[1])));
        }
        return Item.from(XMaterial.valueOf(material));
    }
    public static Item from(Material material) {
        return new Item(new ItemStack(material));
    }
    public static Item from(Material material, int amount) {
        return new Item(new ItemStack(material, amount));
    }
    public static Item from(XMaterial material, int amount) {
        return new Item(material.parseItem(amount));
    }
    public static Item from(MaterialData data) {
        return new Item(data.toItemStack());
    }
    public static Item from(MaterialData data, int amount) {
        return new Item(data.toItemStack(amount));
    }
    public static Item air() {
        return new Item(new ItemStack(Material.AIR));
    }

    public static Item commandIcon(XMaterial material, ICommand command) {
        Item item = new Item(material.parseItem());
        Command commandConfig = command.getClass().getAnnotation(com.xg7plugins.commands.setup.Command.class);
        item.name("&b/&f" + commandConfig.name());
        item.lore(
                "lang:[commands-menu.command-item.usage]",
                "lang:[commands-menu.command-item.description]",
                "lang:[commands-menu.command-item.permission]",
                "lang:[commands-menu.command-item.player-only]",
                "lang:[commands-menu.if-subcommand]"
        );
        item.setBuildPlaceholders(
                new HashMap<String, String>() {
                    {
                        put("[SYNTAX]", commandConfig.syntax());
                        put("[DESCRIPTION]", commandConfig.description());
                        put("[PERMISSION]", commandConfig.permission());
                        put("[PLAYER-ONLY]", String.valueOf(commandConfig.isPlayerOnly()));
                    }
                }
        );
        return item;
    }

    public Item slot(int slot) {
        this.slot = slot;
        return this;
    }

    public ClickableItem clickable() {
        return new ClickableItem(this.itemStack, this.slot);
    }

    public Item setBuildPlaceholders(HashMap<String, String> buildPlaceholders) {
        this.buildPlaceholders = buildPlaceholders;
        return this;
    }

    public Item amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public Item data(MaterialData data) {
        this.itemStack.setData(data);
        return this;
    }
    public Item meta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item name(String name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(name);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item lore(String... lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.stream(lore).collect(Collectors.toList()));
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item lore(String lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item lore(List<String> lore) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item enchants(Map<Enchantment, Integer> enchants) {
        this.itemStack.addUnsafeEnchantments(enchants);
        return this;
    }
    public Item enchant(Enchantment enchant, int level) {
        this.itemStack.addUnsafeEnchantment(enchant, level);
        return this;
    }
    public Item flags(ItemFlag... flags) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addItemFlags(flags);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item customModelData(int data) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setCustomModelData(data);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item unbreakable(boolean unbreakable) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    public Item setNBTTag(String key, Object value) {
        Gson gson = new Gson();

        ReflectionObject nmsItem = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invokeToRObject(this.itemStack);
        ReflectionObject tag = nmsItem.getMethod("getTag").invokeToRObject();

        if (tag.getObject() == null) tag = nbtTagCompoundClass.newInstance();

        String jsonValue = gson.toJson(value);

        tag.getMethod("setString", String.class, String.class).invoke(key, jsonValue);

        nmsItem.getMethod("setTag", nbtTagCompoundClass.getAClass()).invoke(tag.getObject());
        this.itemStack = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass.getAClass()).invoke(nmsItem.getObject());
        return this;
    }
    public static <T> Optional<T> getTag(String key, ItemStack item, Class<T> clazz) {
        Gson gson = new Gson();
        ReflectionObject nmsItem = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invokeToRObject(item);
        ReflectionObject tag = nmsItem.getMethod("getTag").invokeToRObject();

        if (tag != null) {
            String jsonValue = tag.getMethod("getString", String.class).invoke(key);
            return Optional.ofNullable(gson.fromJson(jsonValue, clazz));
        }
        return Optional.empty();
    }

    public <T> Optional<T> getTag(String key, Class<T> clazz) {
        return getTag(key, this.itemStack, clazz);
    }

    @SneakyThrows
    public static ItemStack fromJson(String json) {
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();

        String item64 = object.get("item").getAsString();

        String yaml = new String(Base64.getDecoder().decode(item64));
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(yaml);

        return config.getItemStack("item");
    }

    @Override
    public String toString() {
        return toString(this.itemStack);
    }

    public static String toString(ItemStack stack) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", stack);
        String yaml = config.saveToString();

        Map<String, Object> inventoryItem = new HashMap<>();
        inventoryItem.put("item", Base64.getEncoder().encodeToString(yaml.getBytes()));

        return gson.toJson(inventoryItem);
    }

    public <T extends HumanEntity> ItemStack getItemFor(T player, Plugin plugin) {


            if (this.itemStack.getType().equals(Material.AIR)) return this.itemStack;

            ItemStack prepared = this.itemStack.clone();
            ItemMeta meta = prepared.getItemMeta();
            if (meta.getDisplayName() != null) {
                String newName = Text.format(meta.getDisplayName(), plugin).setReplacements(buildPlaceholders).getWithPlaceholders((Player) player);
                meta.setDisplayName(newName.isEmpty() ? " " : newName);
            }

            if (meta.getLore() != null) {
                List<String> lore = new ArrayList<>();

                for (String line : meta.getLore()) {
                    String formatted = Text.format(line, plugin).setReplacements(buildPlaceholders).getWithPlaceholders((Player) player);
                    if (formatted.isEmpty()) continue;
                    lore.add(formatted);
                }
                meta.setLore(lore);
            }

            prepared.setItemMeta(meta);


            return prepared;

    }

    public boolean isAir() {
        return this.itemStack.getType().equals(Material.AIR);
    }


}
