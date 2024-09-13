package com.xg7plugins.xg7plugins.api.adapted.xg7scores;


import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ScoreManager {

    @Getter
    private static final Set<Score> scoreboards = new HashSet<>();

    @Getter
    private static final List<UUID> sendActionBlackList = new ArrayList<>();

    @Getter
    private static int taskid;

    public static void registerScore(final Score score) {
        scoreboards.add(score);
    }
    public static Score getByPlayer(Player player) {
        return scoreboards.stream().filter(sc -> sc.getPlayers().contains(player)).findFirst().orElse(null);
    }
    public static Score getById(String id) {
        return scoreboards.stream().filter(sc -> sc.getId().equals(id)).findFirst().orElse(null);
    }

    public static void removePlayers() {
        scoreboards.forEach(sc -> sc.getPlayers().forEach(sc::removePlayer));
    }

    public static void cancelTask() {
        Bukkit.getScheduler().cancelTask(taskid);
    }

    public static void initTask() {
        AtomicLong counter = new AtomicLong();
        taskid = Bukkit.getScheduler().runTaskTimerAsynchronously(XG7Scores.getPlugin(), () -> {
            scoreboards.forEach(score -> {

                        Bukkit.getOnlinePlayers().forEach(p -> {
                            if (score.getCondition().condition(p)) score.addPlayer(p);
                            else if (score.getPlayers().contains(p)) score.removePlayer(p);
                        });

                        if (counter.get() % score.getDelay() == 0) {
                            score.update();
                            score.incrementIndex();
                        }

                    }
            );
            counter.incrementAndGet();
        },0,1).getTaskId();
    }

}
