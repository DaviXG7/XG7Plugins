package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.xg7plugins.data.lang.LangEntity;
import com.xg7plugins.xg7plugins.libs.xg7menus.MenuManager;
import com.xg7plugins.xg7plugins.libs.xg7menus.listeners.MenuListener;
import com.xg7plugins.xg7plugins.libs.xg7menus.listeners.PlayerMenuListener;
import com.xg7plugins.xg7plugins.libs.xg7scores.ScoreManager;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.database.DBManager;
import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.events.bukkitevents.EventManager;
import com.xg7plugins.xg7plugins.events.defaultevents.InicializePacketEvents;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventManager1_7;
import com.xg7plugins.xg7plugins.tasks.TaskManager;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter(AccessLevel.PUBLIC)
public final class XG7Plugins extends Plugin {

    private static XG7Plugins instance;

    @Getter
    private static final int minecraftVersion;

    static {
        Pattern pattern = Pattern.compile("1\\.([0-9]?[0-9])");
        Matcher matcher = pattern.matcher(Bukkit.getServer().getVersion());
        matcher.find();
        minecraftVersion = Integer.parseInt(matcher.group(1));
    }

    private DBManager databaseManager;
    private EventManager eventManager;
    private TaskManager taskManager;
    private ScoreManager scoreManager;
    private Object packetEventManager;
    private MenuManager menuManager;

    private final List<Event> events = Arrays.asList(new InicializePacketEvents(), new MenuListener(), new PlayerMenuListener());
    private final List<Config> configs = Collections.singletonList(new Config(this, "config"));

    private final HashMap<String, Plugin> plugins = new HashMap<>();

    public XG7Plugins() {
        super("[XG7Plugins]");
        instance = this;
    }

    @Override
    public void onEnable() {
        this.databaseManager = new DBManager(this);
        this.menuManager = new MenuManager(this);
        this.eventManager = new EventManager();
        this.taskManager = new TaskManager(this);
        this.scoreManager = new ScoreManager(this);
        this.eventManager.registerPlugin(this);
        this.databaseManager.connectPlugin(this);
        EntityProcessor.createTableOf(this, LangEntity.class);
        this.packetEventManager = minecraftVersion < 8 ? new PacketEventManager1_7() : new PacketEventManager();
        if (getConfigsManager().getConfig("config").get("prefix") != null) this.setCustomPrefix(getConfigsManager().getConfig("config").get("prefix"));
    }


    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> ReflectionObject.of(packetEventManager).getMethod("stopEvent", Player.class).invoke(player));
        taskManager.disable();

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
        return configs;
    }

    @Override
    public List<Event> getEvents() {
        return events;
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
