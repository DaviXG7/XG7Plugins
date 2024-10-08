package com.xg7plugins.xg7plugins.libs.xg7holograms.holograms;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.libs.xg7holograms.utils.Location;
import com.xg7plugins.xg7plugins.utils.reflection.EntityDataWatcher;
import com.xg7plugins.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.xg7plugins.utils.text.Text;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram1_7_1_XX extends Hologram {

    public Hologram1_7_1_XX(Plugin plugin, List<String> lines, Location location) {
        super(plugin, lines, location);
    }

    @Override
    public void create(Player player) {



        ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();
        for (int i = 0; i < lines.size(); i++) {
            Location spawnLocation = location.add(0,-i * 0.3,0);

            EntityArmorStand stand = new EntityArmorStand(NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invoke(),
                    spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());


            EntityDataWatcher watcher = new EntityDataWatcher();

            watcher.watch(0 , (byte) 0x20);
            watcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));
            watcher.watch(3, XG7Plugins.getMinecraftVersion() >= 9 ? true : (byte) 1);
            watcher.watch(5, XG7Plugins.getMinecraftVersion() >= 9 ? true : (byte) 1);

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
            playerNMS.sendPacket(packet);

            PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getId(), (DataWatcher) watcher.getWatcher(), true);
            playerNMS.sendPacket(metadata);

            ids.putIfAbsent(player.getUniqueId(), new ArrayList<>());

            ids.get(player.getUniqueId()).add(stand.getId());
        }
    }

    @Override
    public void update(Player player) {
        for (int i = 0; i < lines.size(); i++) {

            EntityDataWatcher dataWatcher = new EntityDataWatcher();

            dataWatcher.watch(2, Text.format(lines.get(i), XG7Plugins.getInstance().getPlugins().getOrDefault(pluginName, XG7Plugins.getInstance())).getWithPlaceholders(player));

            PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(ids.get(player.getUniqueId()).get(i), (DataWatcher) dataWatcher.getWatcher(), true);


            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(metadata);

        }
    }

    @Override
    public void destroy(Player player) {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(ids.get(player.getUniqueId()).stream().mapToInt(i -> i).toArray());
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(destroy);
        ids.remove(player.getUniqueId());
    }
}
