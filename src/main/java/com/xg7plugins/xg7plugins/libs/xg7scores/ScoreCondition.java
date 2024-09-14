package com.xg7plugins.xg7plugins.libs.xg7scores;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ScoreCondition {

    boolean condition(Player player);

}
