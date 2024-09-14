package com.xg7plugins.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import com.xg7plugins.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

public class Tablist extends Score {


    private String[] header;
    private String[] footer;

    private String playerPrefix;
    private String playerSuffix;

    public Tablist(long delay, String[] header, String[] footer, String playerPrefix, String playerSuffix, String id, ScoreCondition condition) {
        super(delay, header.length > footer.length ? header : footer, id, condition);
        if (XG7Plugins.getMinecraftVersion() < 8) throw new RuntimeException("This version doesn't support Tablist");
        this.header = header;
        this.footer = footer;
        this.playerPrefix = playerPrefix;
        this.playerSuffix = playerSuffix;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            player.setPlayerListName(Text.format(playerPrefix).getWithPlaceholders(player) + player.getName() + Text.format(playerSuffix).getWithPlaceholders(player));
            String headerl = header.length <= super.getIndexUpdating() ? header[header.length - 1] : header[super.getIndexUpdating()];
            String footerl = footer.length <= super.getIndexUpdating() ? footer[footer.length - 1] : footer[super.getIndexUpdating()];

            send(player, Text.format(headerl).getText(), Text.format(footerl).getText());
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

        ReflectionObject packetPlayOutListHeaderFooter = NMSUtil.getNMSClass("PacketPlayOutPlayerListHeaderFooter").newInstance();

        packetPlayOutListHeaderFooter.setField("a",NMSUtil.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(header));
        packetPlayOutListHeaderFooter.setField("b",NMSUtil.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(footer));

        PlayerNMS.cast(player).sendPacket(packetPlayOutListHeaderFooter.getObject());
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        send(player,null,null);
        player.setPlayerListName(player.getName());

    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
    }
}
