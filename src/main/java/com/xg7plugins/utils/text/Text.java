package com.xg7plugins.utils.text;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.lang.Lang;
import com.xg7plugins.modules.xg7scores.scores.ActionBar;
import com.xg7plugins.utils.Condition;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.serializer.ComponentDeserializer;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Text {

    private static final Pattern LANG_PATTERN = Pattern.compile("lang:\\[([A-Za-z0-9\\.-]*)\\]");
    private String text;

    public Text(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }

    public Text textFor(Player player) {

        this.text = Condition.processCondition(this.text, player);

        if (XG7Plugins.isPlaceholderAPI()) this.text = PlaceholderAPI.setPlaceholders(player, this.text);

        return this;
    }

    public Text replace(String placeholder, String replacement) {
        this.text = this.text.replace("%" + placeholder + "%", replacement);
        return this;
    }
    @SafeVarargs
    public final Text replaceAll(Pair<String, String>... replacements) {
        for (Pair<String,String> replacement : replacements) {
            this.text = this.text.replace("%" + replacement.getFirst() + "%", replacement.getSecond());
        }
        return this;
    }
    public final Text replaceAll(List<Pair<String,String>> replacements) {
        for (Pair<String,String> replacement : replacements) {
            this.text = this.text.replace("%" + replacement.getFirst() + "%", replacement.getSecond());
        }
        return this;
    }


    public void send(CommandSender sender) {

        if (this.text.isEmpty()) return;

        Component component = ComponentDeserializer.deserialize(this.text);


        send(component,sender);

    }

    public static void send(Component component, CommandSender sender) {
        if (component.isEmpty()) return;

        String rawText = component.content();

        String componentText = component.getText();


        boolean isCentered = rawText.startsWith("<center> ");
        boolean isAction = rawText.startsWith("<action> ");
        boolean isPlayer = sender instanceof Player;

        componentText = componentText.replace("<center> ", "");
        componentText = componentText.replace("<action> ", "");

        rawText = rawText.replace("<center> ", "");
        rawText = rawText.replace("<action> ", "");

        component.setText(componentText);

        if (isCentered) component.center(TextCentralizer.PixelsSize.CHAT);

        if (!isPlayer) {
            if (XG7Plugins.getMinecraftVersion() < 8) {
                sender.sendMessage(rawText);
                return;
            }
            sender.spigot().sendMessage(component.toBukkitComponent());
            return;
        }

        Player player = (Player) sender;

        if (isAction) {
            if (XG7Plugins.getMinecraftVersion() < 8) return;

            ActionBar.addToBlacklist(player);

            if (XG7Plugins.getMinecraftVersion() > 8) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(rawText));
                return;
            }

            WrapperPlayServerChatMessage packetPlayOutChat = new WrapperPlayServerChatMessage(
                    new ChatMessageLegacy(net.kyori.adventure.text.Component.text(rawText), ChatTypes.GAME_INFO)
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packetPlayOutChat);

            Bukkit.getScheduler().runTaskLater(XG7Plugins.getInstance(), () -> ActionBar.removeFromBlacklist(player.getUniqueId()), 60L);

            return;
        }

        if (XG7Plugins.getMinecraftVersion() < 8) {
            player.sendMessage(rawText);
            return;
        }

        player.spigot().sendMessage(component.toBukkitComponent());
    }

    public String getPlainText() {
        try {
            Component component = ComponentDeserializer.deserialize(this.text);

            String rawText = component.content();

            if (rawText.startsWith("<center> ")) rawText = rawText.replace("<center> ", "");
            if (rawText.startsWith("<action> ")) rawText = rawText.replace("<action> ", "");

            return rawText;
        } catch (Exception e) {
            e.printStackTrace();
            return this.text;
        }
    }

    public Component getComponent() {
        return ComponentDeserializer.deserialize(this.text);
    }

    public String getCentralizedText(TextCentralizer.PixelsSize size) {

        Component component = ComponentDeserializer.deserialize(this.text);

        String rawText = component.content();

        if (rawText.startsWith("<center> ")) rawText = rawText.replace("<center> ", "");
        if (rawText.startsWith("<action> ")) rawText = rawText.replace("<action> ", "");

        return TextCentralizer.getCentralizedText(size, rawText);
    }

    public static CompletableFuture<Text> detectLangs(CommandSender sender, Plugin plugin, String rawText, boolean textForSender) {

        return Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).thenApply(lang -> {

            String text = rawText;

            text = text.replace("%plugin%", plugin.getCustomPrefix());
            text = text.replace("%prefix%", sender.getName());

            Matcher langMatch = LANG_PATTERN.matcher(text);

            StringBuilder result = new StringBuilder(text);

            while (langMatch.find()) {
                String path = langMatch.group(1);

                result.replace(langMatch.start(), langMatch.end(), lang.get(path));
            }

            Text objectText = new Text(result.toString());

            if (sender instanceof Player && textForSender) {
                objectText.textFor((Player) sender);
            }

            return objectText;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return new Text("Error: " + throwable.getMessage());
        });
    }
    public static CompletableFuture<Text> detectLangs(CommandSender sender, Plugin plugin, String rawText) {
        return detectLangs(sender, plugin, rawText, true);
    }
    public static CompletableFuture<Text> fromLang(CommandSender sender, Plugin plugin, String path, boolean textForSender) {

        return Lang.of(plugin, !(sender instanceof Player) ? null : (Player) sender).thenApply(lang -> {

            String text = lang.get(path);

            text = text.replace("%plugin%", plugin.getCustomPrefix());
            text = text.replace("%prefix%", sender.getName());

            Text objectText = new Text(text);

            if (sender instanceof Player && textForSender) {
                objectText.textFor((Player) sender);
            }

            return objectText;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return new Text("Error: " + throwable.getMessage());
        });
    }
    public static CompletableFuture<Text> fromLang(CommandSender sender, Plugin plugin, String path) {
        return fromLang(sender, plugin, path, true);
    }

    public static Text format(String text) {
        return new Text(text);
    }

}
