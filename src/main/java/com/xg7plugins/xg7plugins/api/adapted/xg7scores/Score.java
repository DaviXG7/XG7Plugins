package com.xg7plugins.xg7plugins.api.adapted.xg7scores;

import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class Score {

    private boolean updating = false;
    private long delay;
    private String id;
    private String[] toUpdate;
    private int indexUpdating = 0;
    private Set<Player> players;
    private ScoreCondition condition;

    protected Plugin plugin;

    public Score(long delay, String[] toUpdate, String id, ScoreCondition condition, Plugin plugin) {
        this.delay = delay;
        this.toUpdate = toUpdate;
        this.players = new HashSet<>();
        this.id = id;
        this.condition = condition;
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        players.add(player);
        updating = true;
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (players.isEmpty()) updating = false;
    }

    public void incrementIndex() {
        this.indexUpdating++;
        if (indexUpdating == toUpdate.length) indexUpdating = 0;
    }

    public abstract void update();

}
