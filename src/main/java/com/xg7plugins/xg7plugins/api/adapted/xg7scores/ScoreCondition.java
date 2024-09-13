package com.xg7plugins.xg7plugins.api.adapted.xg7scores;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ScoreCondition {

    boolean condition(Player player);

}
