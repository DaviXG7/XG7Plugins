package com.xg7plugins.xg7plugins.libs.xg7scores;

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

    public Score(long delay, String[] toUpdate, String id, ScoreCondition condition) {
        this.delay = delay;
        this.toUpdate = toUpdate;
        this.players = new HashSet<>();
        this.id = id;
        this.condition = condition;
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
