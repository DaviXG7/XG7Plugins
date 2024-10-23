package com.xg7plugins.libs.xg7menus.builders.item;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.libs.xg7menus.builders.BaseItemBuilder;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SkullItemBuilder extends BaseItemBuilder<SkullItemBuilder> {

    private static final Cache<String, ItemMeta> cachedSkulls = Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();

    public SkullItemBuilder(Plugin plugin) {
        super(XMaterial.PLAYER_HEAD.parseItem(),plugin);
    }
    public static SkullItemBuilder builder(Plugin plugin) {
        return new SkullItemBuilder(plugin);
    }
    public SkullItemBuilder renderSkullPlayer() {
        setOwner("THIS_PLAYER");
        return this;
    }
    /**
     * This method sets the skull skin value
     * @param value The skin value of the skull
     * @return This InventoryItem
     */
    public SkullItemBuilder setValue(String value) {
        if (cachedSkulls.asMap().containsKey(value)) {
            this.itemStack.setItemMeta(cachedSkulls.getIfPresent(value));
            return this;
        }
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "null");
        gameProfile.getProperties().put("textures", new Property("textures", value));

        SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        cachedSkulls.put(value, skullMeta);
        super.meta(skullMeta);
        return this;
    }
    /**
     * This method sets the skull owner
     * @param owner The skin owner of the skull
     * @return This InventoryItem
     */
    public SkullItemBuilder setOwner(String owner) {
        if (Bukkit.getOnlineMode() && Bukkit.getPlayer(owner) != null) {
            setPlayerSkinValue(Bukkit.getPlayer(owner).getUniqueId());
            return this;
        }
        if (cachedSkulls.asMap().containsKey(owner)) {
            meta(cachedSkulls.getIfPresent(owner));
            return this;
        }
        SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();
        meta.setOwner(owner);
        cachedSkulls.put(owner, meta);
        super.meta(meta);
        return this;
    }

    /**
     * This method sets the skull skin value with the player skin value
     * @param player The player that will be used to get the skin value
     * @return This InventoryItem
     */
    public SkullItemBuilder setPlayerSkinValue(UUID player) {
        if (cachedSkulls.asMap().containsKey(player.toString())) {
            this.itemStack.setItemMeta(cachedSkulls.getIfPresent(player.toString()));
            return this;
        }
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            if (conn.getResponseCode() != 200) {
                XG7Plugins.getInstance().getLog().severe("Erro ao colocar valor de player na skin da cabeça!");
                return this;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

            JsonObject profileData = new JsonParser().parse(sb.toString()).getAsJsonObject();
            JsonObject properties = profileData.getAsJsonArray("properties").get(0).getAsJsonObject();


            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", properties.get("value").getAsString()));

            SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();


            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            cachedSkulls.put(player.toString(),skullMeta);
            super.meta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
