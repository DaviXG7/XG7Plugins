package com.xg7plugins.xg7plugins.libs.xg7scores;


import com.xg7plugins.xg7plugins.XG7Plugins;
import lombok.Getter;
import net.kyori.adventure.chat.ChatType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class ScoreManager {

    private final XG7Plugins plugin;

    private final HashMap<String, Score> scoreboards = new HashMap<>();
    private final List<UUID> sendActionBlackList = new ArrayList<>();

    private UUID taskId;

    public ScoreManager(XG7Plugins plugin) {
        this.plugin = plugin;
    }

    public void registerScore(final Score score) {
        scoreboards.put(score.getId(), score);
    }
    public Score getByPlayer(Player player) {
        return scoreboards.values().stream().filter(sc -> sc.getPlayers().contains(player)).findFirst().orElse(null);
    }
    public Score getById(String id) {
        return scoreboards.get(id);
    }

    public void removePlayers() {
        scoreboards.values().forEach(sc -> sc.getPlayers().forEach(sc::removePlayer));
    }

    public void cancelTask() {
        plugin.getTaskManager().cancelTask(plugin, this.taskId);
    }

    public void initTask() {
        AtomicLong counter = new AtomicLong();
        this.taskId = plugin.getTaskManager().addRepeatingTask(plugin, () -> {
            scoreboards.values().forEach(score -> {

                        Bukkit.getOnlinePlayers().forEach(p -> {
                            if (score.getCondition().condition(p)) {
                                score.addPlayer(p);
                            }
                            else if (score.getPlayers().contains(p)) score.removePlayer(p);
                        });

                        if (counter.get() % score.getDelay() == 0) {
                            score.update();
                            score.incrementIndex();
                        }

                    }
            );
            counter.incrementAndGet();
        },1);
    }

}
