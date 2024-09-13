package com.xg7plugins.xg7plugins.api.adapted.xg7scores.scores;

import com.xg7plugins.xg7entities.api.XG7Scores;
import com.xg7plugins.xg7plugins.api.adapted.xg7scores.*;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class Tablist extends Score {


    private String[] header;
    private String[] footer;

    private String playerPrefix;
    private String playerSuffix;

    public Tablist(long delay, String[] header, String[] footer, String playerPrefix, String playerSuffix, String id, ScoreCondition condition) {
        super(delay, header.length > footer.length ? header : footer, id, condition);
        if (XG7Scores.getVersion() < 8) throw new RuntimeException("This version doesn't support Tablist");
        this.header = header;
        this.footer = footer;
        this.playerPrefix = playerPrefix;
        this.playerSuffix = playerSuffix;
        ScoreManager.registerScore(this);
    }

    @Override
    public void update() {
        for (Player player : super.getPlayers()) {
            player.setPlayerListName(Text.format(playerPrefix).setPlaceholders(player).getText() + player.getName() + Text.format(playerSuffix).setPlaceholders(player).getText());
            String headerl = header.length <= super.getIndexUpdating() ? header[header.length - 1] : header[super.getIndexUpdating()];
            String footerl = footer.length <= super.getIndexUpdating() ? footer[footer.length - 1] : footer[super.getIndexUpdating()];

            send(player, Text.format(headerl).getText(), Text.format(footerl).getText());
        }
    }

    @SneakyThrows
    public void send(Player player, String header, String footer) {

        if (header == null) header = "";
        if (footer == null) footer = "";

        if (Integer.parseInt(Bukkit.getVersion().split("\\.")[1].replace(")", "")) >= 13) {
            player.setPlayerListHeader(Text.format(header).setPlaceholders(player).getText());
            player.setPlayerListFooter(Text.format(footer).setPlaceholders(player).getText());
            return;
        }

        Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
        Object craftPlayer = craftPlayerClass.cast(player);
        Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
        Object playerConnection = craftPlayerHandle.getClass().getField("playerConnection").get(craftPlayerHandle);

        Class<?> PlacketPlayOutPlayerListHeaderFooterClass = NMSUtil.getNMSClass("PacketPlayOutPlayerListHeaderFooter");
        Class<?> chatComponentTextClass = NMSUtil.getNMSClass("ChatComponentText");

        Object headerComponent = chatComponentTextClass.getConstructor(String.class).newInstance(Text.format(header).setPlaceholders(player).getText());
        Object footerComponent = chatComponentTextClass.getConstructor(String.class).newInstance(Text.format(footer).setPlaceholders(player).getText());

        Object PlacketPlayOutPlayerListHeaderFooterInstance = PlacketPlayOutPlayerListHeaderFooterClass.newInstance();

        Field fieldA = PlacketPlayOutPlayerListHeaderFooterClass.getDeclaredField("a");
        fieldA.setAccessible(true);
        fieldA.set(PlacketPlayOutPlayerListHeaderFooterInstance, headerComponent);

        Field fieldB = PlacketPlayOutPlayerListHeaderFooterClass.getDeclaredField("b");
        fieldB.setAccessible(true);
        fieldB.set(PlacketPlayOutPlayerListHeaderFooterInstance, footerComponent);

        playerConnection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(playerConnection, PlacketPlayOutPlayerListHeaderFooterInstance);
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
