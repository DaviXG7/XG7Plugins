package com.xg7plugins.xg7plugins.events.bukkitevents;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.events.Event;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class EventManager {

    private final HashMap<Plugin, Listener> listeners = new HashMap<>();

    public void registerPlugin(Plugin plugin) {

        if (plugin.getEvents().isEmpty()) return;

        listeners.put(plugin, new Listener() {});

        for (Event event : plugin.getEvents()) {

            if (!event.isEnabled()) continue;
            for (Method method : event.getClass().getMethods()) {
                if (!method.isAnnotationPresent(EventHandler.class)) continue;
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);

                plugin.getServer().getPluginManager().registerEvent(
                        (Class<? extends org.bukkit.event.Event>) method.getParameterTypes()[0],
                        listeners.get(plugin),
                        eventHandler.priority(),
                        (listener, event2) ->
                                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                    if (eventHandler.isOnlyInWorld()) {
                                        if (event2 instanceof PlayerEvent) {
                                            PlayerEvent playerEvent = (PlayerEvent) event2;
                                            if (!plugin.getEnabledWorlds().contains(playerEvent.getPlayer().getWorld().getName()))
                                                return;
                                        }
                                        if (event2 instanceof WorldEvent) {
                                            WorldEvent worldEvent = (WorldEvent) event2;
                                            if (!plugin.getEnabledWorlds().contains(worldEvent.getWorld().getName()))
                                                return;
                                        }
                                        if (event2 instanceof BlockEvent) {
                                            BlockEvent blockEvent = (BlockEvent) event2;
                                            if (!plugin.getEnabledWorlds().contains(blockEvent.getBlock().getWorld().getName()))
                                                return;
                                        }
                                    }

                                    if (method.getParameterTypes().length == 2) {
                                        try {
                                            method.invoke(event, event2, plugin);
                                        } catch (IllegalAccessException | InvocationTargetException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                        return;
                                    }

                                    try {
                                        method.invoke(event, event2);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        throw new RuntimeException(e);
                                    }
                                }),
                        plugin

                );
            }


        }
    }

    public void unregisterEvents(Plugin plugin) {
        HandlerList.unregisterAll(listeners.get(plugin));
    }

}
