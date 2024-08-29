package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.interfaces.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandSetup(
        name = "nomezintestezin",
        description = "Descrição",
        syntax = "/nomezintestezin [Player]",
        aliases = {"nmztz"}
)
public class CommandExample implements ICommand {

    @Override
    public ISubCommand[] getSubCommands() {
        return new ISubCommand[]{new SubCommandExample()};
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.HOPPER);
    }

    //Se não tiver nenhum argumento
    @Override
    @CommandConfig(
            perm = "permissão",
            isOnlyInWorld = false,
            isOnlyPlayer = true
    )
    public void onCommand(Command command, CommandSender sender, String label) {

    }

    static class SubCommandExample implements ISubCommand {

        @Override
        public ItemStack getIcon() {
            return new ItemStack(Material.BARRIER);
        }
        @Override
        public SubCommandType getType() {
            return SubCommandType.PLAYER;
        }

        @Override
        @SubCommandConfig(
                perm = "perm2",
                isOnlyPlayer = true
        )
        public void onSubCommand(CommandSender sender, Player target, String label) {

        }
    }

}
