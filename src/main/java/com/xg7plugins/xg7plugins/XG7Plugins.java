package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.utils.reflection.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.List;

public final class XG7Plugins extends JavaPlugin {


    List<Plugin> plugins;


    @Override
    public void onEnable() {
        // Plugin startup logic

        Player player = null;

        ReflectionObject world = NMSUtil.getNMSClass("World").castToRObject(ReflectionMethod.of(player.getWorld(), "getHandle").invoke());

        ReflectionObject wither = NMSUtil.getNMSClass("EntityWither").getConstructor(world.getClass()).newInstance(world);

        PlayerNMS nms = PlayerNMS.cast(player);

        ReflectionObject dataWatcher = NMSUtil.getNMSClass("DataWatcher").getConstructor(NMSUtil.getNMSClass("Entity").getAClass()).newInstance(NMSUtil.getNMSClass("Entity").cast(null));

        dataWatcher.getMethod("a", int.class, Object.class).invoke(6 , (float) (healthPercent * 200) / 100);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(10, getToUpdate()[0]);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(2, getToUpdate()[0]);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(11, (byte) 1);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(3, (byte) 1));
        dataWatcher.getMethod("a", int.class, Object.class).invoke(17, 0);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(18, 0);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(19, 0);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(20, 1000);
        dataWatcher.getMethod("a", int.class, Object.class).invoke(0, (byte) (1 << 5));

        Class<?> packetClass = NMSUtil.getNMSClass("PacketPlayOutSpawnEntityLiving");
        Object packet = packetClass.getConstructor(NMSUtil.getNMSClass("EntityLiving")).newInstance(wither);


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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
