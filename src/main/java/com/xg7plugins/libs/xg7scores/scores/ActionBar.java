package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ActionBar extends Score {

    public ActionBar(long delay, String[] text, String id, ScoreCondition condition, Plugin plugin) {
        super(delay, text, id, condition, plugin);
        if (XG7Plugins.getMinecraftVersion() < 8) throw new RuntimeException("This version doesn't support ActionBar");
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public void update() {
        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            if (XG7Plugins.getInstance().getScoreManager().getSendActionBlackList().contains(player.getUniqueId())) continue;
            Text.format(super.getToUpdate()[super.getIndexUpdating()],plugin).sendScoreActionBar(player);
        }
    }


}
