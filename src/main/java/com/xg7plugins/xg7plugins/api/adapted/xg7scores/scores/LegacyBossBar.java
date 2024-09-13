package com.xg7plugins.xg7plugins.api.adapted.xg7scores.scores;

import com.xg7plugins.xg7plugins.api.adapted.xg7scores.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class LegacyBossBar extends Score {

    private float healthPercent;

    private final HashMap<UUID, Integer> entities = new HashMap<>();

    @SneakyThrows
    public LegacyBossBar(long delay, String[] text, String id, ScoreCondition condition, float healthPercent) {
        super(delay, text, id, condition);
        this.healthPercent = healthPercent;
        ScoreManager.registerScore(this);
    }

    @SneakyThrows
    @Override
    public void addPlayer(Player player) {
        if (!super.getPlayers().contains(player)) {
            super.addPlayer(player);

            Class<?> witherClass = NMSUtil.getNMSClass("EntityWither");
            Object nmsWorld = NMSUtil.getNMSClass("World").cast(player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld()));
            Object wither = witherClass.getConstructor(NMSUtil.getNMSClass("World")).newInstance(nmsWorld);

            Class<?> packetClass = NMSUtil.getNMSClass("PacketPlayOutSpawnEntityLiving");
            Object packet = packetClass.getConstructor(NMSUtil.getNMSClass("EntityLiving")).newInstance(wither);

            Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = craftPlayerHandle.getClass().getField("playerConnection").get(craftPlayerHandle);


            Class<?> packetMetaClass = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata");

            Class<?> dataWatcherClass = NMSUtil.getNMSClass("DataWatcher");

            Object dataWatcher = dataWatcherClass.getConstructor(NMSUtil.getNMSClass("Entity")).newInstance(NMSUtil.getNMSClass("Entity").cast(null));

            Method watchMethod = dataWatcher.getClass().getMethod("a", int.class, Object.class);
            watchMethod.invoke(dataWatcher, 6, (float) (healthPercent * 200) / 100);

            watchMethod.invoke(dataWatcher, 10, getToUpdate()[0]);
            watchMethod.invoke(dataWatcher, 2, getToUpdate()[0]);

            watchMethod.invoke(dataWatcher, 11, (byte) 1);
            watchMethod.invoke(dataWatcher, 3, (byte) 1);

            watchMethod.invoke(dataWatcher, 17, 0);
            watchMethod.invoke(dataWatcher, 18, 0);
            watchMethod.invoke(dataWatcher, 19, 0);

            watchMethod.invoke(dataWatcher, 20, 1000);
            watchMethod.invoke(dataWatcher, 0, (byte) (1 << 5));


            Object packetMeta = packetMetaClass.getConstructor(int.class, dataWatcherClass, boolean.class)
                    .newInstance((int) witherClass.getMethod("getId").invoke(wither), dataWatcher, true);

            entities.put(player.getUniqueId(), (int) witherClass.getMethod("getId").invoke(wither));

            playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, packet);

            playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, packetMeta);
        }


    }

    @SneakyThrows
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        Class<?> packetDestroyClass = NMSUtil.getNMSClass("PacketPlayOutEntityDestroy");
        Object packetDestroy = packetDestroyClass.getConstructor(int[].class).newInstance(new int[]{entities.get(player.getUniqueId())});

        Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
        Object craftPlayer = craftPlayerClass.cast(player);
        Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
        Object playerConnection = craftPlayerHandle.getClass().getField("playerConnection").get(craftPlayerHandle);

        playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, packetDestroy);

        entities.remove(player.getUniqueId());
    }


    @SneakyThrows
    @Override
    public void update() {

        for (Player player : super.getPlayers()) {

            Location playerLocation = player.getLocation();
            Vector direction = playerLocation.getDirection();

            Location targetLocation = playerLocation.add(direction.multiply(40));

            Class<?> packetTeleportClass = NMSUtil.getNMSClass("PacketPlayOutEntityTeleport");

            Object packetTeleport = packetTeleportClass.getConstructor().newInstance();

            Field a = packetTeleportClass.getDeclaredField("a");
            Field b = packetTeleportClass.getDeclaredField("b");
            Field c = packetTeleportClass.getDeclaredField("c");
            Field d = packetTeleportClass.getDeclaredField("d");
            Field e = packetTeleportClass.getDeclaredField("e");
            Field f = packetTeleportClass.getDeclaredField("f");

            a.setAccessible(true);
            b.setAccessible(true);
            c.setAccessible(true);
            d.setAccessible(true);
            e.setAccessible(true);
            f.setAccessible(true);

            a.set(packetTeleport, entities.get(player.getUniqueId()));
            b.set(packetTeleport, (int) (targetLocation.getX() * 32D));
            c.set(packetTeleport, (int) (targetLocation.getY() * 32D));
            d.set(packetTeleport, (int) (targetLocation.getZ() * 32D));
            e.set(packetTeleport, (byte) (int) (targetLocation.getYaw() * 256F / 360F));
            f.set(packetTeleport, (byte) (int) (targetLocation.getPitch() * 256F / 360F));

            Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = craftPlayerHandle.getClass().getField("playerConnection").get(craftPlayerHandle);

            playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, packetTeleport);

            Class<?> packetMetaClass = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata");

            Class<?> dataWatcherClass = NMSUtil.getNMSClass("DataWatcher");

            Object dataWatcher = dataWatcherClass.getConstructor(NMSUtil.getNMSClass("Entity")).newInstance(NMSUtil.getNMSClass("Entity").cast(null));

            Method watchMethod = dataWatcher.getClass().getMethod("a", int.class, Object.class);

            watchMethod.invoke(dataWatcher, 10, Text.format(getToUpdate()[getIndexUpdating()]).setPlaceholders(player).getText());
            watchMethod.invoke(dataWatcher, 2, Text.format(getToUpdate()[getIndexUpdating()]).setPlaceholders(player).getText());

            Object packetMeta = packetMetaClass.getConstructor(int.class, dataWatcherClass, boolean.class)
                    .newInstance(entities.get(player.getUniqueId()), dataWatcher, true);


            playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, packetMeta);


        }

    }
}
