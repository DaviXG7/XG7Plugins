package com.xg7plugins.xg7plugins.api.adapted.xg7scores.scores;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.api.adapted.xg7scores.*;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.entity.Player;

public class ActionBar extends Score {

    public ActionBar(long delay, String[] text, String id, ScoreCondition condition) {
        super(delay, text, id, condition);
        if (XG7Plugins.getMinecraftVersion() < 8) throw new RuntimeException("This version doesn't support ActionBar");
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            if (XG7Plugins.getInstance().getScoreManager().getSendActionBlackList().contains(player.getUniqueId())) continue;
            Text.format(super.getToUpdate()[super.getIndexUpdating()]).sendScoreActionBar(player);
        }
    }


}
