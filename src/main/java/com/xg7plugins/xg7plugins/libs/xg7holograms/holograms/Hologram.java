package com.xg7plugins.xg7plugins.libs.xg7holograms.holograms;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.libs.xg7holograms.utils.Location;
import com.xg7plugins.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionMethod;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.xg7plugins.utils.text.Text;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public abstract class Hologram {

    protected List<String> lines;
    protected Location location;
    protected UUID id;
    protected String pluginName;

    protected transient Map<UUID, List<Integer>> ids = new HashMap<>();

    public Hologram(Plugin plugin, List<String> lines, Location location) {
        if (XG7Plugins.getMinecraftVersion() <= 7) {
            plugin.getLog().severe("Holograms are not supported in this version.");
            return;
        }
        this.pluginName = plugin.getName();
        this.id = UUID.randomUUID();
        this.lines = lines;
        this.location = location;
        XG7Plugins.getInstance().getHologramsManager().addHologram(this);
    }

    public abstract void create(Player player);
    public abstract void update(Player player);
    public abstract void destroy(Player player);

}
