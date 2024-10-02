package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.libs.xg7geyserforms.builders.ComponentFactory;
import com.xg7plugins.xg7plugins.libs.xg7geyserforms.builders.FormCreator;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram1_8_1_16;
import com.xg7plugins.xg7plugins.libs.xg7holograms.utils.Location;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7scores.builder.BossBarBuilder;
import com.xg7plugins.xg7plugins.libs.xg7scores.builder.ScoreBuilder;
import com.xg7plugins.xg7plugins.utils.Conversation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;

import java.util.Arrays;

@Command(
        name = "teste",
        description = "Teste",
        syntax = "/teste",
        aliasesPath = "teste",
        perm = "xg7plugins.command.teste",
        isOnlyPlayer = true
)
public class TesteCommand implements ICommand {

    private static Hologram hologram;

    @Override
    public ISubCommand[] getSubCommands() {
        return new ISubCommand[]{new Create(), new Update(), new Destroy()};
    }

    @Override
    public void onCommand(org.bukkit.command.Command command, Player player, String label) {
        Conversation
                .create(XG7Plugins.getInstance())
                .errorMessage("§cVocê digitou um valor inválido")
                .addPrompt("Digite um número", Conversation.ResultType.INTEGER)
                .addPrompt("Digite um texto", Conversation.ResultType.STRING)
                .cancelWord("cancelar")
                .onAbandon(conversationAbandonedEvent -> {
                    if (conversationAbandonedEvent.gracefulExit()) return;
                    player.sendMessage("§cVocê abandonou a conversa");
                })
                .onFinish(conversationResult -> {
                    player.sendMessage("§aNúmero: " + conversationResult.get(0));
                    player.sendMessage("§aTexto: " + conversationResult.get(1));
                })
                .start(player);

    }

    @Override
    public ItemBuilder getIcon() {
        return null;
    }

    @SubCommand(
            name = "create",
            description = "create",
            syntax = "/teste create",
            perm = "",
            type = SubCommandType.PLAYER
    )
    static class Create implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {
            hologram = new Hologram1_8_1_16(XG7Plugins.getInstance(), Arrays.asList("§aTeste 1", "§bTeste 2", "§cTeste 3", "lang:[formated-name]"), Location.fromBukkit(((Player)target).getLocation()));
            hologram.create((Player) target);
        }

        @Override
            public ItemBuilder getIcon() {
                return null;
            }
    }
    @SubCommand(
            name = "update",
            description = "update",
            syntax = "/teste update",
            perm = "",
            type = SubCommandType.PLAYER
    )
    static class Update implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {
            hologram.update((Player) target);
        }

        @Override
        public ItemBuilder getIcon() {
            return null;
        }
    }
    @SubCommand(
            name = "destroy",
            description = "destroy",
            syntax = "/teste destroy",
            perm = "",
            type = SubCommandType.PLAYER
    )
    static class Destroy implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {
            hologram.destroy((Player) target);
        }

        @Override
        public ItemBuilder getIcon() {
            return null;
        }
    }
}
