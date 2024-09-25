package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.setup.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@Command(
        name = "nomezintestezin",
        descriptionPath = "lang.path",
        syntax = "/nomezintestezin [Player]",
        aliasesPath = "nmztz",
        perm = "a"
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
    public void onCommand(org.bukkit.command.Command command, CommandSender sender, String label) {

    }

    @SubCommand(
            perm = "perm2",
            isOnlyPlayer = true,
            type = SubCommandType.PLAYER,
            syntax = "/exemple, perm2"
    )
    static class SubCommandExample implements ISubCommand {

        @Override
        public ItemStack getIcon() {
            return new ItemStack(Material.BARRIER);
        }

        @Override
        public void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {

        }
    }

}
