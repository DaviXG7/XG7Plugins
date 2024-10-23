package com.xg7plugins.events.defaultevents;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.events.Event;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.reflection.ReflectionObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuit implements Event {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        XG7Plugins.getInstance().getPacketEventManager().create(event.getPlayer());
        XG7Plugins.getInstance().getHologramsManager().addPlayer(event.getPlayer());
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        XG7Plugins plugin = XG7Plugins.getInstance();
        plugin.getPacketEventManager().stopEvent(event.getPlayer());
        plugin.getMenuManager().removePlayerFromAll(event.getPlayer());
        plugin.getHologramsManager().removePlayer(event.getPlayer());

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getScoreManager().removePlayer(event.getPlayer()), 2L);

    }
}
