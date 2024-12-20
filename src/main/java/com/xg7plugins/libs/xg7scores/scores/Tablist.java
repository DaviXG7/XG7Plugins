package com.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.Plugin;
import com.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Tablist extends Score {

    private static ReflectionObject packetPlayOutListHeaderFooter;

    static {
        try {
            packetPlayOutListHeaderFooter = NMSUtil.getNMSClass("PacketPlayOutPlayerListHeaderFooter").newInstance();
        } catch (Exception ignored) {
            //Not is the version
        }
    }

    private final String[] header;
    private final String[] footer;

    private final String playerPrefix;
    private final String playerSuffix;

    public Tablist(long delay, String[] header, String[] footer, String playerPrefix, String playerSuffix, String id, ScoreCondition condition, Plugin plugin) {
        super(delay, header.length > footer.length ? header : footer, id, condition, plugin);
        if (XG7Plugins.getMinecraftVersion() < 8) throw new RuntimeException("This version doesn't support Tablist");
        this.header = header;
        this.footer = footer;
        this.playerPrefix = playerPrefix;
        this.playerSuffix = playerSuffix;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public void update() {
        for (UUID id : super.getPlayers()) {
            Player player = Bukkit.getPlayer(id);
            if (player == null) continue;
            player.setPlayerListName(Text.format(playerPrefix,plugin).getWithPlaceholders(player) + player.getName() + Text.format(playerSuffix,plugin).getWithPlaceholders(player));
            String headerl = header.length <= super.getIndexUpdating() ? header[header.length - 1] : header[super.getIndexUpdating()];
            String footerl = footer.length <= super.getIndexUpdating() ? footer[footer.length - 1] : footer[super.getIndexUpdating()];

            send(player, Text.format(headerl,plugin).getText(), Text.format(footerl,plugin).getText());
        }
    }

    @SneakyThrows
    public void send(Player player, String header, String footer) {

        if (header == null) header = "";
        if (footer == null) footer = "";

        if (XG7Plugins.getMinecraftVersion() >= 13) {
            player.setPlayerListHeader(header);
            player.setPlayerListFooter(footer);
            return;
        }

        packetPlayOutListHeaderFooter.setField("a", NMSUtil.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(header).getObject());
        packetPlayOutListHeaderFooter.setField("b", NMSUtil.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(footer).getObject());

        PlayerNMS.cast(player).sendPacket(packetPlayOutListHeaderFooter.getObject());
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        send(player,"","");
        player.setPlayerListName(player.getName());

    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
    }
}
