package com.xg7plugins.xg7plugins.libs.xg7holograms.holograms;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.libs.xg7holograms.utils.Location;
import com.xg7plugins.xg7plugins.utils.reflection.*;
import com.xg7plugins.xg7plugins.utils.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram1_8_1_16 extends Hologram {

    public Hologram1_8_1_16(Plugin plugin, List<String> names, Location location) {
        super(plugin, names, location);
    }

    @Override
    public void create(Player player) {
        try {
            ReflectionObject nmsWorld = NMSUtil.getCraftBukkitClass("CraftWorld").castToRObject(location.getWorld()).getMethod("getHandle").invokeToRObject();
            for (int i = 0; i < names.size(); i++) {

                Location spawnLocation = location.add(0,-i * 0.3,0);

                ReflectionObject armorStand = NMSUtil.getNMSClass("EntityArmorStand")
                        .getConstructor(NMSUtil.getNMSClass("World").getAClass(), double.class, double.class, double.class)
                        .newInstance(nmsWorld.getObject(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());

                armorStand.getMethod("setInvisible", boolean.class).invoke(true);
                armorStand.getMethod("setCustomName", String.class).invoke(Text.format(names.get(i),plugin).getWithPlaceholders(player));
                armorStand.getMethod("setCustomNameVisible", boolean.class).invoke(true);
                armorStand.getMethod("setGravity", boolean.class).invoke(false);

                ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutSpawnEntityLiving")
                        .getConstructor(NMSUtil.getNMSClass("EntityLiving").getAClass())
                        .newInstance(armorStand.getObject());

                PlayerNMS playerNMS = PlayerNMS.cast(player);
                playerNMS.sendPacket(packet.getObject());

                ids.putIfAbsent(player.getUniqueId(), new ArrayList<>());

                ids.get(player.getUniqueId()).add(armorStand.getMethod("getId").invoke());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void destroy(Player player) {
        ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutEntityDestroy")
                .getConstructor(int[].class)
                .newInstance(ids.get(player.getUniqueId()).stream().mapToInt(i -> i).toArray());
        PlayerNMS playerNMS = PlayerNMS.cast(player);
        playerNMS.sendPacket(packet.getObject());
    }

    @Override
    public void update(Player player) {

        for (int i = 0; i < names.size(); i++) {

            ReflectionObject dataWatcher = NMSUtil.getNMSClass("DataWatcher").getConstructor(NMSUtil.getNMSClass("Entity").getAClass()).newInstance(NMSUtil.getNMSClass("Entity").cast(null));

            ReflectionMethod aMethod = dataWatcher.getMethod("a", int.class, Object.class);

            aMethod.invoke(2, Text.format(names.get(i),plugin).getWithPlaceholders(player));
            aMethod.invoke(3, (byte) (ChatColor.stripColor(Text.format(names.get(i), plugin).getWithPlaceholders(player)).isEmpty() ? 0 : 1));

            ReflectionObject packetPlayOutEntityMetaData = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata")
                    .getConstructor(int.class, dataWatcher.getObjectClass(), boolean.class)
                    .newInstance(ids.get(player.getUniqueId()).get(i), dataWatcher.getObject(), true);

            PlayerNMS playerNMS = PlayerNMS.cast(player);
            playerNMS.sendPacket(packetPlayOutEntityMetaData.getObject());

        }

    }
}
