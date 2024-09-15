package com.xg7plugins.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.entity.Player;

public class XPBar extends Score {

    public XPBar(long delay, String[] numbers, String id, ScoreCondition condition, Plugin plugin) {
        super(delay, numbers, id, condition, plugin);
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            player.setLevel(Integer.parseInt(Text.format(getToUpdate()[getIndexUpdating()].split(", ")[0],plugin).getWithPlaceholders(player)));
            player.setExp(Float.parseFloat(Text.format(getToUpdate()[getIndexUpdating()].split(", ")[1],plugin).getWithPlaceholders(player)));
        }
    }
}
