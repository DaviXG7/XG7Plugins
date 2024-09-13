package com.xg7plugins.xg7plugins.api.adapted.xg7scores.scores;

import com.xg7plugins.xg7plugins.api.adapted.xg7scores.Score;
import com.xg7plugins.xg7plugins.api.adapted.xg7scores.ScoreCondition;
import com.xg7plugins.xg7plugins.api.adapted.xg7scores.ScoreManager;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBar extends Score {

    private Map<UUID, org.bukkit.boss.BossBar> bossBars = new HashMap<>();

    private BarColor color;
    private BarStyle style;
    private double progress;

    public BossBar(long delay, String id, ScoreCondition condition, String[] title, BarColor color, BarStyle style, double progress) {
        super(delay, title, id, condition);
        this.color = color;
        this.style = style;
        this.progress = progress;
        ScoreManager.registerScore(this);
    }
    @SneakyThrows
    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        if (!bossBars.containsKey(player.getUniqueId())) {
            bossBars.put(player.getUniqueId(), Bukkit.createBossBar(getToUpdate()[0],color,style));
            bossBars.get(player.getUniqueId()).setProgress(progress);
            bossBars.get(player.getUniqueId()).addPlayer(player);
        }
    }

    @SneakyThrows
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        bossBars.get(player.getUniqueId()).removePlayer(player);
        bossBars.remove(player.getUniqueId());
    }
    @Override
    public void update() {

        for (Player player : super.getPlayers()) if (!bossBars.get(player.getUniqueId()).getTitle().equals(Text.format(getToUpdate()[getIndexUpdating()]).setPlaceholders(player).getText())) bossBars.get(player.getUniqueId()).setTitle(Text.format(getToUpdate()[getIndexUpdating()]).setPlaceholders(player).getText());
    }
}
