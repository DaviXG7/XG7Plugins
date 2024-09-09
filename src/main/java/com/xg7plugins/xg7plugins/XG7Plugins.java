package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.interfaces.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.database.DBManager;
import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.xg7plugins.events.defaultevents.InicializePacketEvents;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventManager1_7;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
public final class XG7Plugins extends Plugin {

    @Getter
    private static final int minecraftVersion = Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", ""));

    private DBManager databaseManager;
    private EventManager eventManager;
    private Object packetEventManager;

    private final HashMap<String, Plugin> plugins = new HashMap<>();

    public XG7Plugins() {
        super("[XG7Plugins]");
    }

    @Override
    public void onEnable() {
        this.databaseManager = new DBManager(this);
        this.eventManager = new EventManager();
        this.eventManager.registerPlugin(this);
        this.packetEventManager = minecraftVersion < 8 ? new PacketEventManager1_7() : new PacketEventManager();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> ReflectionObject.of(packetEventManager).getMethod("stopEvent", Player.class).invoke(player));
    }

    @Override
    public void onLoad() {

    }

    @Override
    public List<ICommand> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public List<Config> getConfigs() {
        return Arrays.asList(new Config(this, "config"));
    }

    @Override
    public List<Event> getEvents() {
        return Arrays.asList(new InicializePacketEvents());
    }

    @Override
    public List<Event> getPacketEvents() {
        return Collections.emptyList();
    }

    public static void register(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName(), plugin);

        xg7Plugins.getDatabaseManager().connectPlugin(plugin);
        xg7Plugins.getEventManager().registerPlugin(plugin);
        ReflectionObject.of(xg7Plugins.getPacketEventManager()).getMethod("registerPlugin", Plugin.class).invoke(plugin);
    }

    public static void unregister(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        ReflectionObject.of(xg7Plugins.getPacketEventManager()).getMethod("unregisterPlugin", Plugin.class).invoke(plugin);
        xg7Plugins.getDatabaseManager().disconnectPlugin(plugin);

        xg7Plugins.getPlugins().remove(plugin.getName());

    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }
}
