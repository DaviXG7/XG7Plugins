package com.xg7plugins.xg7plugins.utils.Text;

import com.xg7plugins.xg7plugins.XG7Plugins;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextComponent {

    private static final Pattern pattern = Pattern.compile("\\[(CLICK|HOVER|CLICKHOVER)(.*?)](.*?)\\[/\\1]", Pattern.DOTALL);
    private static final Pattern value = Pattern.compile("value=%(.*?)%");
    private static final Pattern textP = Pattern.compile("text=%(.*?)%");
    private static final Pattern action = Pattern.compile("action=%(.*?)%");


    private final String text;
    private final String rawText;

    public TextComponent(String text) {

        String rawText = text.replaceAll("\\[(CLICK|HOVER|CLICKHOVER)(.*?)](.*?)\\[/\\1]", "$3");

        if (rawText.startsWith("[CENTER] ")) rawText = rawText.substring(9);

        this.text = text;
        this.rawText = rawText;

    }

    public void send(Player player) {

        String transletedRawText = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? PlaceholderAPI.setPlaceholders(player, rawText) : rawText;

        String transletedText = Text.getSpacesCentralized(Text.PixelsSize.CHAT.getPixels(), transletedRawText) + (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? PlaceholderAPI.setPlaceholders(player, text) : text);

        if (XG7Plugins.getMinecraftVersion() < 8) {
            player.sendMessage(transletedRawText);
            return;
        }

        Matcher matcher = pattern.matcher(transletedText);

        int lastIndex = 0;

        ComponentBuilder builder = new ComponentBuilder();

        while (matcher.find()) {

            if (lastIndex < matcher.start()) {

                String noTagText = transletedText.substring(lastIndex, matcher.start());
                builder.append(noTagText);
            }



            String tagName = matcher.group(1);
            String attributes = matcher.group(2).trim();
            String content = matcher.group(3).trim();

            net.md_5.bungee.api.chat.TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent(content);

            Matcher valMatch = value.matcher(attributes);
            Matcher textMatch = textP.matcher(attributes);
            Matcher actionMatch = action.matcher(attributes);

            switch (tagName) {
                case "CLICK":
                    if (!valMatch.find() || !actionMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Click tag with content " + content + " have a syntax error!");
                        return;
                    }


                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(actionMatch.group(0)), valMatch.group(0)));

                    builder.append(textComponent);
                    continue;

                case "HOVER":
                    if (!textMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Hover tag with content " + content + " have a syntax error!");
                        return;
                    }


                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(textMatch.group(0))));

                    builder.append(textComponent);
                    continue;
                case "CLICKHOVER":
                    if (!valMatch.find() || !actionMatch.find() || !textMatch.find()) {
                        XG7Plugins.getInstance().getLog().warn("Click and hover tag with content " + content + " have a syntax error!");
                        return;
                    }


                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(textMatch.group(0))));

                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(actionMatch.group(0)), valMatch.group(0)));

                    builder.append(textComponent);
                    continue;


            }

            lastIndex = matcher.end();
        }

        if (lastIndex < text.length()) {
            String noTagText = transletedText.substring(lastIndex, matcher.start());
            builder.append(noTagText);
        }

        player.spigot().sendMessage(builder.create());

    }


}
