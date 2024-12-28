package com.xg7plugins.libs.xg7holograms;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HologramsManager {

    private final XG7Plugins plugin;

    private final Task task;

    public HologramsManager(XG7Plugins plugin) {
        this.plugin = plugin;
        long delay = plugin.getConfigsManager().getConfig("config").getTime("holograms-update-delay").orElse(100L);
        this.task = new Task(
                plugin,
                "holograms",
                true,
                true,
                delay,
                TaskState.IDLE,
                () -> holograms.values().forEach(hologram -> Bukkit.getOnlinePlayers().forEach(player -> {
                    World world = hologram.getLocation().getWorld();
                    if (!player.getWorld().equals(world) && hologram.getIds().containsKey(player.getUniqueId())) {
                        hologram.destroy(player);
                        return;
                    }
                    if (player.getWorld().equals(world) && !hologram.getIds().containsKey(player.getUniqueId())) {
                        hologram.create(player);
                        return;
                    }
                    hologram.update(player);
                }))
        );
    }

    private HashMap<String, Hologram> holograms = new HashMap<>();

    public Hologram getHologramById(Player player, int id) {

        return holograms.values().stream().filter(hologram -> !hologram.getIds().containsKey(player.getUniqueId()) ? null : hologram.getIds().get(player.getUniqueId()).contains(id)).findFirst().orElse(null);
    }

    public void addHologram(Hologram hologram) {
        holograms.put(hologram.getId(), hologram);
    }
    public void removeHologram(Hologram hologram) {
        holograms.remove(hologram.getId());
    }
    public void addPlayer(Player player) {
        holograms.values().forEach(hologram -> hologram.create(player));
    }
    public void removePlayer(Player player) {
        holograms.values().forEach(hologram -> hologram.destroy(player));
    }
    public void cancelTask() {
        plugin.getTaskManager().cancelTask(task);
    }


}
