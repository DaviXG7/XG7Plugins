package com.xg7plugins.xg7plugins.libs.xg7holograms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HologramsManager {

    private List<Hologram> holograms = new ArrayList<>();
    private final XG7Plugins plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HologramsManager(XG7Plugins plugin) {
        this.plugin = plugin;
        load();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(new File(plugin.getDataFolder(), "data/holograms.json"))) {
            gson.toJson(holograms,writer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void load() {
        Type listType = new TypeToken<List<Hologram>>() {}.getType();

        try (FileReader reader = new FileReader(new File(plugin.getDataFolder(), "data/holograms.json"))) {
            this.holograms = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public Hologram getHologramById(Player player, int id) {
        return holograms.stream().filter(hologram -> hologram.getIds().get(player.getUniqueId()).contains(id)).findFirst().orElse(null);
    }

    public void addHologram(Hologram hologram) {
        holograms.add(hologram);
    }


}
