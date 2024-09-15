package com.xg7plugins.xg7plugins.events.defaultevents;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InicializePacketEvents implements Event {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ReflectionObject.of(XG7Plugins.getInstance().getPacketEventManager()).getMethod("create", Player.class).invoke(event.getPlayer());
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        ReflectionObject.of(XG7Plugins.getInstance().getPacketEventManager()).getMethod("stopEvent", Player.class).invoke(event.getPlayer());
    }
}
