package com.xg7plugins.xg7plugins.api.adapted.xg7scores.scores;

import com.xg7plugins.xg7entities.api.XG7Scores;
import com.xg7plugins.xg7plugins.api.adapted.xg7scores.*;
import org.bukkit.entity.Player;

public class ActionBar extends Score {

    public ActionBar(long delay, String[] text, String id, ScoreCondition condition) {
        super(delay, text, id, condition);
        if (XG7Scores.getVersion() < 8) throw new RuntimeException("This version doesn't support ActionBar");
        ScoreManager.registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            if (ScoreManager.getSendActionBlackList().contains(player.getUniqueId())) continue;
            Text.format(super.getToUpdate()[super.getIndexUpdating()]).setPlaceholders(player).sendScoreActionBar(player);
        }
    }


}
