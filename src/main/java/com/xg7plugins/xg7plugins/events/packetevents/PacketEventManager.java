package com.xg7plugins.xg7plugins.events.packetevents;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.utils.reflection.PlayerNMS;
import io.netty.channel.*;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketEventManager {

    private final List<Event> packetEvents = new ArrayList<>();

    public void registerPlugin(Plugin plugin) {

        if (plugin.getPacketEvents().isEmpty()) return;

        for (Event event : plugin.getPacketEvents()) {
            if (!event.isEnabled()) continue;
            this.packetEvents.add(event);
        }

        Bukkit.getOnlinePlayers().forEach(this::create);
    }

    @SneakyThrows
    public void create(Player player) {

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet)
                    throws Exception {

                Object modPacket = packet;

                for (Event event : packetEvents) {
                    for (Method method : event.getClass().getMethods()) {
                        if (!method.isAnnotationPresent(PacketEventHandler.class)) continue;
                        PacketEventHandler eventHandler = method.getAnnotation(PacketEventHandler.class);
                        if (packet.getClass().getName().endsWith(eventHandler.packetName())) modPacket = method.invoke(event, player, packet);
                    }
                }

                super.channelRead(context, packet);
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {

                Object modPacket = packet;

                for (Event event : packetEvents) {
                    for (Method method : event.getClass().getMethods()) {
                        if (!method.isAnnotationPresent(PacketEventHandler.class)) continue;
                        PacketEventHandler eventHandler = method.getAnnotation(PacketEventHandler.class);
                        if (packet.getClass().getName().endsWith(eventHandler.packetName())) modPacket = method.invoke(event, player, packet);
                    }
                }

                super.write(context, packet, channelPromise);
            }
        };

        PlayerNMS playerNMS = PlayerNMS.cast(player);

        Channel channel = (Channel) Arrays.stream(playerNMS.getNetworkManager().getObjectClass().getDeclaredFields()).filter(field -> {
            field.setAccessible(true);
            return field.getType().getName().endsWith("Channel");
        }).findFirst().orElse(null).get(playerNMS.getNetworkManager().getObject());

        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    public static void stopEvent(Player player) {
        try {
            PlayerNMS playerNMS = PlayerNMS.cast(player);
            Channel channel = (Channel) Arrays.stream(playerNMS.getNetworkManager().getObjectClass().getDeclaredFields()).filter(field -> {
                field.setAccessible(true);
                return field.getType().getName().endsWith("Channel");
            }).findFirst().orElse(null).get(playerNMS.getNetworkManager().getObject());

            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void unregisterPlugin(Plugin plugin) {
        plugin.getPacketEvents().forEach(this.packetEvents::remove);
    }

}
