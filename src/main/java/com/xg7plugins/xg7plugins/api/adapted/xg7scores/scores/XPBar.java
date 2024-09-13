package com.xg7plugins.xg7plugins.api.adapted.xg7scores.scores;

import com.xg7plugins.xg7plugins.api.adapted.xg7scores.Score;
import com.xg7plugins.xg7plugins.api.adapted.xg7scores.ScoreCondition;
import com.xg7plugins.xg7plugins.api.adapted.xg7scores.ScoreManager;
import org.bukkit.entity.Player;

public class XPBar extends Score {

    public XPBar(long delay, String[] numbers, String id, ScoreCondition condition) {
        super(delay, numbers, id, condition);
        ScoreManager.registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            player.setLevel(Integer.parseInt(Text.format(getToUpdate()[getIndexUpdating()].split(", ")[0]).setPlaceholders(player).getText()));
            player.setExp(Float.parseFloat(Text.format(getToUpdate()[getIndexUpdating()].split(", ")[1]).setPlaceholders(player).getText()));
        }
    }
}
