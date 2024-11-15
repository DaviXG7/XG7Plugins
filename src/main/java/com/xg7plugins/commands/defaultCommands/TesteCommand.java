package com.xg7plugins.commands.defaultCommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.setup.*;
import com.xg7plugins.libs.xg7holograms.HologramBuilder;
import com.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.libs.xg7npcs.NPCBuilder;
import com.xg7plugins.libs.xg7npcs.npcs.NPC;
import com.xg7plugins.utils.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Command(
        name = "teste",
        description = "Teste",
        syntax = "/teste",
        aliasesPath = "teste",
        perm = "xg7plugins.command.teste",
        isOnlyPlayer = true
)
public class TesteCommand implements ICommand {

    private static com.xg7plugins.libs.xg7npcs.npcs.NPC npc;


    @Override
    public ISubCommand[] getSubCommands() {
        return new ISubCommand[]{new Conversation(), new Hologram(), new NPC()};
    }

    @Override
    public ItemBuilder getIcon() {
        return null;
    }

    @SubCommand(
            name = "conversation",
            description = "Cria uma conversa",
            syntax = "/teste conversation",
            perm = "",
            type = SubCommandType.NORMAL,
            isOnlyPlayer = true
    )
    static class Conversation implements ISubCommand {

        @Override
        public ItemBuilder getIcon() {
            return null;
        }

        public void onSubCommand(CommandSender sender, String[] args, String label) {
            Player player = (Player) sender;
            com.xg7plugins.utils.Conversation
                    .create(XG7Plugins.getInstance())
                    .errorMessage("§cVocê digitou um valor inválido")
                    .addPrompt("Digite um número", com.xg7plugins.utils.Conversation.ResultType.INTEGER)
                    .addPrompt("Digite um texto", com.xg7plugins.utils.Conversation.ResultType.STRING)
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
    }
    @SubCommand(
            name = "hologram",
            description = "Cria um holograma",
            syntax = "/teste hologram",
            perm = "",
            type = SubCommandType.NORMAL,
            isOnlyPlayer = true
    )
    static class Hologram implements ISubCommand {

        @Override
        public ItemBuilder getIcon() {
            return null;
        }

        public void onSubCommand(CommandSender sender, String[] args, String label) {
            XG7Plugins.getInstance().getHologramsManager().initTask();
            HologramBuilder
                    .creator(XG7Plugins.getInstance(), "teste")
                    .setLocation(Location.fromPlayer((Player) sender))
                    .addLine("§aTeste")
                    .addLine("lang:[example]")
                    .addLine("§cTeste")
                    .build();
        }
    }
    @SubCommand(
            name = "npc",
            description = "Cria um NPC",
            syntax = "/teste npc",
            perm = "",
            type = SubCommandType.NORMAL,
            isOnlyPlayer = true
    )
    static class NPC implements ISubCommand {

        @Override
        public ItemBuilder getIcon() {
            return null;
        }

        public void onSubCommand(CommandSender sender, String[] args, String label) {

            if (args.length == 0) {
                sender.sendMessage("§cUse /teste npc <skin | tp | create | destroy | equip>");
                return;
            }

            switch (args[1]) {
                case "create":
                    XG7Plugins.getInstance().getNpcManager().initTask();
                    try {
                        npc = NPCBuilder.creator(XG7Plugins.getInstance(), "teste")
                                .setLocation(Location.fromPlayer((Player) sender))
                                .lookAtPlayer(true)
                                .setChestplate(new ItemStack(org.bukkit.Material.DIAMOND_CHESTPLATE))
                                .setName("1","2","3","4")
                                .build();
                        System.out.println("Construiu?");
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    npc.setLookAtPlayer(true);
                    npc.setSkin((Player) sender);
                    break;
                case "destroy":
                    npc.destroy((Player) sender);
                    break;
                case "remove":
                    npc.remove();
                    break;
                case "tp":
                    npc.teleport(Location.fromPlayer((Player) sender));
                    break;
                case "skin":
                    try {
                        npc.setSkin(args[2]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage("Não foi possível setar a skin");
                    }
                    break;
                case "equip":
                    try {
                        Player player = (Player) sender;
                        player.sendMessage("AAAAAAAAAAAAAAAAAAAAAAAA");
                        npc.setEquipment(player.getItemInHand(), null, player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }




        }
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.Command command, CommandSender sender, String label, String[] args) {
        return args.length == 1 ? Arrays.asList("conversation", "hologram", "npc") : Arrays.asList("skin", "tp", "create", "destroy", "equip");
    }
}
