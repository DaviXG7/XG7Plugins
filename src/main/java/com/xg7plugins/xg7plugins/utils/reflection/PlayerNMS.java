package com.xg7plugins.xg7plugins.utils.reflection;

import com.xg7plugins.xg7plugins.XG7Plugins;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public class PlayerNMS {

    private Player player;
    private ReflectionObject craftPlayerHandle;
    private ReflectionObject playerConnection;
    private ReflectionObject networkManager;

    @SneakyThrows
    public static PlayerNMS cast(Player player) {

        ReflectionObject craftPlayer = NMSUtil.getCraftBukkitClass("entity.CraftPlayer").castToRObject(player);

        ReflectionObject handle = craftPlayer.getMethod("getHandle").invokeToRObject();
        Object playerConnection = Arrays.stream(handle.getObjectClass().getFields()).filter(field -> field.getType().getName().endsWith("PlayerConnection")).findFirst().orElse(null).get(handle.getObject());
        Object networkManager = Arrays.stream(playerConnection.getClass().getFields()).filter(field -> field.getType().getName().endsWith("NetworkManager")).findFirst().orElse(null).get(playerConnection);

        return new PlayerNMS(player, handle, ReflectionObject.of(playerConnection), ReflectionObject.of(networkManager));

    }

    public void sendPacket(Object packet) {
        ReflectionMethod.of(playerConnection, "sendPacket", NMSUtil.getNMSClass("Packet").getAClass()).invoke(packet);
    }



}
