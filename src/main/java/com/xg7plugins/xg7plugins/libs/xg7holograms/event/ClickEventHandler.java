package com.xg7plugins.xg7plugins.libs.xg7holograms.event;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventHandler;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClickEventHandler implements Event {

    @Override
    public boolean isEnabled() {
        return true;
    }


    @PacketEventHandler(packet = "PacketPlayInUseEntity")
    public Object onClick(Player player, ReflectionObject packet) {
        try {
            Hologram hologram = XG7Plugins.getInstance().getHologramsManager().getHologramById(player, packet.getField("a"));

            if (hologram == null) return packet.getObject();

            Enum<?> enumAction = packet.getField("action");

            ClickType type;

            switch (enumAction.name()) {
                case "INTERACT_AT":
                case "INTERACT":
                    type = player.isSneaking() ? ClickType.SHIFT_RIGHT_CLICK : ClickType.RIGHT_CLICK;
                    break;
                case "ATTACK":
                    type = player.isSneaking() ? ClickType.SHIFT_LEFT_CLICK : ClickType.LEFT_CLICK;
                    break;
                default:
                    type = ClickType.RIGHT_CLICK;
                    break;
            }


            Bukkit.getScheduler().runTask(XG7Plugins.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new HologramClickEvent(player, type, hologram)));

            return packet.getObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet.getObject();
    }
}
