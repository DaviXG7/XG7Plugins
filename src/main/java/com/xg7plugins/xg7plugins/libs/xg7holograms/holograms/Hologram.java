package com.xg7plugins.xg7plugins.libs.xg7holograms.holograms;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.libs.xg7holograms.ClickEvent;
import com.xg7plugins.xg7plugins.libs.xg7holograms.utils.Location;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public abstract class Hologram {

    protected List<String> names;
    protected Location location;
    protected Plugin plugin;
    protected Consumer<ClickEvent> onClick;

    protected transient Map<UUID, List<Integer>> ids;

    public Hologram(Plugin plugin,List<String> names, Location location) {
        this.plugin = plugin;
        this.names = names;
        this.location = location;
        this.ids = new HashMap<>();
    }
    public void onClick(Consumer<ClickEvent> onClick) {
        this.onClick = onClick;
    }

    public abstract void create(Player player);
    public abstract void destroy(Player player);
    public abstract void update(Player player);

}
