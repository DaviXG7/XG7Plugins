package com.xg7plugins.xg7plugins.events.packetevents;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.events.Event;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PacketManagerBase {

    protected final List<Event> packetEvents = new ArrayList<>();


    public void registerPlugin(Plugin plugin) {
        if (plugin.getPacketEvents().isEmpty()) return;

        for (Event event : plugin.getPacketEvents()) {
            if (!event.isEnabled()) continue;
            this.packetEvents.add(event);
        }
    }

    public abstract void stopEvent(Player player);

    public void unregisterPlugin(Plugin plugin) {
        plugin.getPacketEvents().forEach(this.packetEvents::remove);
    }

    public abstract void create(Player player);

}
