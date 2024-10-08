package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram1_7_1_XX;
import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram1_8_1_16;
import com.xg7plugins.xg7plugins.libs.xg7holograms.utils.Location;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.utils.Conversation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return new ISubCommand[]{new Create(), new Update(), new Destroy(), new Save()};
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
            type = SubCommandType.NORMAL
    )
    static class Create implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, String[] args, String label) {
            try {
                System.out.println(Bukkit.getPlayer(args[1]).getName());
                hologram = new Hologram1_7_1_XX(XG7Plugins.getInstance(), Arrays.asList("§aTeste 1", "§bTeste 2", "§cTeste 3", "lang:[formated-name]"), Location.fromBukkit(Bukkit.getPlayer(args[1]).getLocation()));
                System.out.println(">>>>");

                hologram.create(Bukkit.getPlayer(args[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            type = SubCommandType.NORMAL
    )
    static class Update implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, String[] args, String label) {
            try {
                hologram.update(Bukkit.getPlayer(args[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            type = SubCommandType.NORMAL
    )
    static class Destroy implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, String[] args, String label) {
            try {
                hologram.destroy(Bukkit.getPlayer(args[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public ItemBuilder getIcon() {
            return null;
        }
    }
    @SubCommand(
            name = "save",
            description = "save",
            syntax = "/teste save",
            perm = "",
            type = SubCommandType.NORMAL
    )
    static class Save implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, String[] args, String label) {
            try {
                sender.sendMessage("Salvo!!!!!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public ItemBuilder getIcon() {
            return null;
        }
    }
}
