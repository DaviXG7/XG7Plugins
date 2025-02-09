package com.xg7plugins;

import com.xg7plugins.data.playerdata.PlayerData;
import com.xg7plugins.tasks.TaskState;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XG7PluginsPlaceholderExpansion extends PlaceholderExpansion {

    private static final Pattern pattern = Pattern.compile("format\\((.*?)\\)");

    @Override
    public @NotNull String getIdentifier() {
        return "xg7plugins";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DaviXG7";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {

        if (player == null) return null;

        switch (identifier) {
            case "tasks_running":
                return XG7Plugins.getInstance().getTaskManager().getTasks().values().stream().filter(task -> task.getState() == TaskState.RUNNING).count() + "";
            case "tasks_idle":
                return XG7Plugins.getInstance().getTaskManager().getTasks().values().stream().filter(task -> task.getState() == TaskState.IDLE).count() + "";
        }

        if (identifier.startsWith("player_")) {
            PlayerData playerData = XG7Plugins.getInstance().getPlayerDataDAO().get(player.getUniqueId()).join();

            if (playerData == null) return null;

            if (identifier.equals("player_lang")) return playerData.getLangId();

            if (identifier.startsWith("player_firstjoin_")) {
                //player_firstjoin_format(dd/MM/yyyy HH:mm:ss)

                String format = identifier.split("_")[2];

                switch (format) {
                    case "millis":
                        return (playerData.getFirstJoin()) + "";
                    case "seconds":
                        return ((int) (playerData.getFirstJoin() / 1000)) + "";
                    case "minutes":
                        return ((int) (playerData.getFirstJoin() / 1000 / 60)) + "";
                    case "hours":
                        return ((int) (playerData.getFirstJoin() / 1000 / 60 / 60)) + "";
                    case "days":
                        return ((int) (playerData.getFirstJoin() / 1000 / 60 / 60 / 24)) + "";
                    default:

                        Matcher matcher = pattern.matcher(format);

                        if (!matcher.find()) return null;

                        SimpleDateFormat sdf = new SimpleDateFormat(matcher.group(1));
                        return sdf.format(playerData.getFirstJoin());
                }
            }
        }

        return null;
    }
}
