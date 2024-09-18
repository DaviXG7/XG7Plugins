package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventHandler;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventExample implements Event {
    @Override
    public boolean isEnabled() {
        return true;
    }


    @EventHandler
    public void event(PlayerMoveEvent e) {
        e.setCancelled(true);
    }

    @PacketEventHandler(packetsName = {
            "PacketPlayOut1",
            "PacketPlayOut2"
    })
    public Object packetEvent(Object packet) {
        return packet;
    }
}
