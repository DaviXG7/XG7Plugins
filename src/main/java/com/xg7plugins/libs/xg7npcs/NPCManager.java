package com.xg7plugins.libs.xg7npcs;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.libs.xg7npcs.npcs.NPC;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class NPCManager {
    @Getter
    private final HashMap<String, NPC> npcs = new HashMap<>();
    @Getter
    private HashMap<Integer, Object> lookingNPCS = null;
    private final XG7Plugins plugin;
    @Getter
    private final Task task;

    public NPCManager(XG7Plugins plugin) {
        this.plugin = plugin;
        long delay = plugin.getConfigsManager().getConfig("config").getTime("npcs-update-delay").orElse(10000L);
        this.task = new Task(
                plugin,
                "npcs",
                true,
                true,
                delay,
                TaskState.IDLE,
                () -> npcs.values().forEach(npc -> Bukkit.getOnlinePlayers().forEach(player -> {
                    World world = npc.getLocation().getWorld();
                    if (!player.getWorld().equals(world) && npc.getNpcIDS().containsKey(player.getUniqueId())) {
                        npc.destroy(player);
                        return;
                    }
                    if (player.getWorld().equals(world) && !npc.getNpcIDS().containsKey(player.getUniqueId())) {
                        npc.spawn(player);
                    }
                }))
        );
    }

    public NPC getNPCByID(UUID uuid) {
        return npcs.get(uuid);
    }
    public NPC getNPCByID(Player player, int id) {
        return npcs.values().stream().filter(hologram -> hologram.getNpcIDS().get(player.getUniqueId()) == id).findFirst().orElse(null);
    }

    public void addNPC(NPC npc) {
        npcs.put(npc.getId(), npc);
    }
    public void removeNPC(NPC npc) {
        npcs.remove(npc.getId());
    }

    public void addPlayer(Player player) {
        npcs.values().forEach(hologram -> hologram.spawn(player));
    }
    public void removePlayer(Player player) {
        npcs.values().forEach(hologram -> hologram.destroy(player));
    }


    public void registerLookingNPC(int id, Object entity) {
        if (!(boolean)plugin.getConfigsManager().getConfig("config").get("npcs-look-at-player", Boolean.class).orElse(false)) return;

        if (lookingNPCS == null) {
            lookingNPCS = new HashMap<>();
        }
        lookingNPCS.put(id, entity);
    }
    public void unregisterLookingNPC(int id) {
        if (!(boolean)plugin.getConfigsManager().getConfig("config").get("npcs-look-at-player", Boolean.class).orElse(false)) return;

        lookingNPCS.remove(id);
        if (lookingNPCS.isEmpty()) {
            lookingNPCS = null;
        }
    }
    public void cancelTask() {
        plugin.getTaskManager().cancelTask(task);
    }
}
