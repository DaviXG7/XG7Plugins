package com.xg7plugins.xg7plugins;

import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@Command(
        name = "nomezintestezin",
        description = "lang.path",
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
    public ItemBuilder getIcon() {
        return null;
    }

    //Se não tiver nenhum argumento
    @Override
    public void onCommand(org.bukkit.command.Command command, CommandSender sender, String label) {

    }

    @SubCommand(
            perm = "perm2",
            description = "a description",
            isOnlyPlayer = true,
            type = SubCommandType.PLAYER,
            syntax = "/exemple, perm2"
    )
    static class SubCommandExample implements ISubCommand {

        @Override
        public ItemBuilder getIcon() {
            return null;
        }

        @Override
        public void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {

        }
    }

}
