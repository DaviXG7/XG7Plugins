package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.defaultCommands.LangCommand;
import com.xg7plugins.xg7plugins.commands.defaultCommands.ReloadCommand;
import com.xg7plugins.xg7plugins.commands.defaultCommands.TaskCommands;
import com.xg7plugins.xg7plugins.commands.defaultCommands.TesteCommand;
import com.xg7plugins.xg7plugins.data.database.EntityProcessor;
import com.xg7plugins.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.xg7plugins.events.packetevents.PacketManagerBase;
import com.xg7plugins.xg7plugins.libs.xg7geyserforms.FormManager;
import com.xg7plugins.xg7plugins.libs.xg7holograms.ClickEventHandler;
import com.xg7plugins.xg7plugins.libs.xg7holograms.HologramsManager;
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
import com.xg7plugins.xg7plugins.events.defaultevents.JoinAndQuit;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventManager;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventManager1_7;
import com.xg7plugins.xg7plugins.tasks.TaskManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter(AccessLevel.PUBLIC)
public final class XG7Plugins extends Plugin {

    private static XG7Plugins instance;

    @Getter
    private static final int minecraftVersion;
    @Getter
    private static final boolean floodgate;
    @Getter
    private static final boolean placeholderAPI;

    static {
        Pattern pattern = Pattern.compile("1\\.([0-9]?[0-9])");
        Matcher matcher = pattern.matcher(Bukkit.getServer().getVersion());
        minecraftVersion = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;

        floodgate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private DBManager databaseManager;
    private EventManager eventManager;
    private TaskManager taskManager;
    private ScoreManager scoreManager;
    private PacketManagerBase packetEventManager;
    private MenuManager menuManager;
    private FormManager formManager;
    private HologramsManager hologramsManager;

    private List<Event> events;
    private List<ICommand> commands;
    private List<Config> configs;

    private final HashMap<String, Plugin> plugins = new HashMap<>();

    public XG7Plugins() {
        super("[XG7Plugins]", new String[] {"pt-br", "en-us"});
        instance = this;
    }

    @Override
    public void onEnable() {
        Config config = getConfigsManager().getConfig("config");
        if (config.get("prefix") != null) this.setCustomPrefix(ChatColor.translateAlternateColorCodes('&', config.get("prefix")));
        this.databaseManager = new DBManager(this);
        this.menuManager = new MenuManager(this);
        this.eventManager = new EventManager();
        this.packetEventManager = minecraftVersion < 8 ? new PacketEventManager1_7() : new PacketEventManager();
        Bukkit.getOnlinePlayers().forEach(player -> packetEventManager.create(player));
        this.taskManager = new TaskManager(this);
        this.scoreManager = new ScoreManager(this);
        this.eventManager.registerPlugin(this);
        this.packetEventManager.registerPlugin(this);
        this.databaseManager.connectPlugin(this);
        this.formManager = floodgate ? new FormManager() : null;
        EntityProcessor.createTableOf(this, PlayerLanguage.class);
    }


    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> packetEventManager.stopEvent(player));
        scoreManager.removePlayers();
        taskManager.getExecutor().shutdown();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public List<ICommand> getCommands() {
        if (commands == null) commands = Arrays.asList(new LangCommand(), new ReloadCommand(), new TaskCommands(), new TesteCommand());
        return commands;
    }

    @Override
    public List<Config> getConfigs() {
        if (configs == null) configs = Arrays.asList(new Config(this, "config"),new Config(this, "commands"));
        return configs;
    }

    @Override
    public List<Event> getEvents() {
        if (events == null) events = Arrays.asList(new JoinAndQuit(), new MenuListener(), new PlayerMenuListener(menuManager));
        return events;
    }

    @Override
    public List<Event> getPacketEvents() {
        return Collections.singletonList(new ClickEventHandler());
    }

    @Override
    public String[] dependencies() {
        return new String[0];
    }

    public static void register(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPlugins().put(plugin.getName().split(" ")[0], plugin);

        xg7Plugins.getDatabaseManager().connectPlugin(plugin);
        xg7Plugins.getEventManager().registerPlugin(plugin);
        xg7Plugins.getPacketEventManager().registerPlugin(plugin);
    }

    public static void unregister(Plugin plugin) {
        XG7Plugins xg7Plugins = XG7Plugins.getInstance();

        xg7Plugins.getPacketEventManager().unregisterPlugin(plugin);
        xg7Plugins.getDatabaseManager().disconnectPlugin(plugin);
        xg7Plugins.getScoreManager().unregisterPlugin(plugin);

        xg7Plugins.getPlugins().remove(plugin.getName());

    }

    public static @NotNull XG7Plugins getInstance() {
        return getPlugin(XG7Plugins.class);
    }
}
