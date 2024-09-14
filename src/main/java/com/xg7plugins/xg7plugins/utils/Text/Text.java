package com.xg7plugins.xg7plugins.utils.Text;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.config.Configs;
import com.xg7plugins.xg7plugins.utils.Log;
import com.xg7plugins.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Text {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("\\[g#([0-9a-fA-F]{6})](.*?)\\[/g#([0-9a-fA-F]{6})]");
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private String text;

    public Text(String text, Plugin plugin) {
        if (XG7Plugins.getMinecraftVersion() >= 16) {
            text = applyGradients();
            Matcher matcher = HEX_PATTERN.matcher(text);
            while (matcher.find()) {
                String color = text.substring(matcher.start(), matcher.end());
                text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color.substring(1)) + "");
                matcher = HEX_PATTERN.matcher(text);
            }
        }

        this.text = ChatColor.translateAlternateColorCodes('&', text.replace("[PREFIX]", plugin.getCustomPrefix()));
    }

    public static Text format(String text, Plugin plugin) {
        return new Text(text,plugin);
    }
    public static com.xg7plugins.xg7plugins.utils.Text.TextComponent fromConfig(Config config, String path) {
        Text text1 = new Text(config.get(path), config.getPlugin());
        return new com.xg7plugins.xg7plugins.utils.Text.TextComponent(text1.getText());
    }

    public String getWithPlaceholders(Player player) {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? PlaceholderAPI.setPlaceholders((OfflinePlayer) player, text) : text;
    }


    public void send(CommandSender sender) {
        if (text == null || text.isEmpty()) return;

        if (sender instanceof Player) {

            String transleted = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, text) : text;

            text = text.replace("[PLAYER]", sender.getName());
            if (text.startsWith("[ACTION] ")) {
                sendActionBar(transleted.substring(9), ((Player) sender));
                return;
            }

            sender.sendMessage(getCentralizedText(PixelsSize.CHAT.pixels, text));
            return;
        }
        sender.sendMessage(getCentralizedText(PixelsSize.CHAT.pixels, text));
    }

    @SneakyThrows
    public void sendActionBar(Player player) {

        if (XG7Plugins.getMinecraftVersion() < 8) return;

        XG7Plugins.getInstance().getScoreManager().getSendActionBlackList().add(player.getUniqueId());

        sendScoreActionBar(player);

        Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> XG7Plugins.getInstance().getScoreManager().getSendActionBlackList().remove(player.getUniqueId()),60L);

    }

    @SneakyThrows
    public void sendScoreActionBar(Player player) {

        if (XG7Plugins.getMinecraftVersion() < 8) return;

        String finalText = text;

        if (text.startsWith("[ACTION] ")) {
            finalText = finalText.substring(9);
            return;
        }

        if (XG7Plugins.getMinecraftVersion() > 8) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(finalText));
            return;
        }

        ReflectionObject chatComponent = NMSUtil.getNMSClass("IChatBaseComponent").getConstructor(String.class).newInstance(finalText);

        ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutChat")
                .getConstructor(NMSUtil.getNMSClass("IChatBaseComponent").getAClass(), byte.class)
                .newInstance(chatComponent.getObject(), (byte) 2);

        PlayerNMS.cast(player).sendPacket(packet.getObject());

    }

    public static String getCentralizedText(int pixels, String text) {
        return getCentralizedSpaces(pixels,text) + text;

    }


    public static String getSpacesCentralized(int pixels, String text) {

        if (!text.startsWith("[CENTER] ")) return text;
        text = text.substring(9);

        int textWidht = 0;
        boolean cCode = false;
        boolean isBold = false;
        boolean isrgb = false;
        int rgbCount = 0;
        int cCodeCount = 0;
        int rgbToAdd = 0;
        for (char c : text.toCharArray()) {
            if (isrgb) {
                if (rgbCount == 6) {
                    isrgb = false;
                    continue;
                }
                if ("0123456789aAbBcCdDeEfF".contains(String.valueOf(c))) {
                    rgbToAdd = getCharSize(c, isBold);
                    rgbCount++;
                    continue;
                }
                rgbCount = 0;
                textWidht += rgbToAdd;
                continue;
            }
            if (c == '&') {
                cCode = true;
                cCodeCount++;
                continue;
            }
            if (cCode && net.md_5.bungee.api.ChatColor.ALL_CODES.contains(c + "")) {
                cCode = false;
                cCodeCount = 0;
                isBold = c == 'l' || c == 'L';
                continue;
            }
            if (cCode) {
                if (c == '#') {
                    cCode = false;
                    isrgb = true;
                    continue;
                }
                while (cCodeCount != 0) {
                    cCodeCount--;
                    textWidht += getCharSize('&', isBold);
                }
            }
            textWidht += getCharSize(c, isBold);
        }

        textWidht /= 2;

        if (textWidht > pixels) return text;

        StringBuilder builder = new StringBuilder();

        int compensated = 0;

        while (compensated < pixels - textWidht) {
            builder.append(ChatColor.COLOR_CHAR + "r ");
            compensated += 4;
        }

        return builder.toString();

    }
    public static String getCentralizedSpaces(int pixels, String text) {
        return getCentralizedText(pixels,text).replace(text, "");
    }

    private double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    private String applyGradients() {

        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            Color from = Color.decode("#" + matcher.group(1));
            Color to = Color.decode("#" + matcher.group(3));
            String textHex = matcher.group(2);

            double[] red = linear(from.getRed(), to.getRed(), textHex.length());
            double[] green = linear(from.getGreen(), to.getGreen(), textHex.length());
            double[] blue = linear(from.getBlue(), to.getBlue(), textHex.length());

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < textHex.length(); i++) {
                builder.append(net.md_5.bungee.api.ChatColor.of(new Color(
                                (int) Math.round(red[i]),
                                (int) Math.round(green[i]),
                                (int) Math.round(blue[i]))))
                        .append(textHex.charAt(i));
            }
            matcher.appendReplacement(result, builder.toString() + net.md_5.bungee.api.ChatColor.RESET);
        }
        matcher.appendTail(result);

        return result.toString();
    }
    private static int getCharSize(char c, boolean isBold) {
        String[] chars = new String[]{"~@", "1234567890ABCDEFGHJKLMNOPQRSTUVWXYZabcedjhmnopqrsuvxwyz/\\+=-_^?&%$#", "{}fk*\"<>()", "It[] ", "'l`", "!|:;,.i", "¨´"};
        for (int i = 0; i < chars.length; i++) {
            if (chars[i].contains(String.valueOf(c))) {
                return isBold && c != ' ' ? 8 - i : 7 - i;
            }
        }

        return 4;
    }


    @Getter
    public enum PixelsSize {

        CHAT(157),
        MOTD(127),
        INV(75);

        final int pixels;

        PixelsSize (int pixels) {
            this.pixels = pixels;
        }

    }

    public static long convertToMilliseconds(Plugin plugin, String timeStr) {
        long milliseconds = 0;
        Pattern pattern = Pattern.compile("(\\d+)([SMHD])");
        Matcher matcher = pattern.matcher(timeStr.toUpperCase());

        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "S":
                    milliseconds += value * 1000;
                    break;
                case "M":
                    milliseconds += value * 60000;
                    break;
                case "H":
                    milliseconds += value * 3600000;
                    break;
                case "D":
                    milliseconds += value * 86400000;
                    break;
                default:
                    plugin.getLog().severe("Invalid time unit: " + unit);
            }
        }

        return milliseconds;
    }


}
