package com.xg7plugins.xg7plugins.libs.xg7holograms;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class HologramsManager {

    private HashMap<UUID, Hologram> holograms = new HashMap<>();

    public Hologram getHologramById(Player player, int id) {
        return holograms.values().stream().filter(hologram -> hologram.getIds().get(player.getUniqueId()).contains(id)).findFirst().orElse(null);
    }

    public void addHologram(Hologram hologram) {
        holograms.put(hologram.getId(), hologram);
    }
    public void addPlayer(Player player) {
        holograms.values().forEach(hologram -> hologram.create(player));
    }
    public void removePlayer(Player player) {
        holograms.values().forEach(hologram -> hologram.destroy(player));
    }


}
