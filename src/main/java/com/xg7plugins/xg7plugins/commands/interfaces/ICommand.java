package com.xg7plugins.xg7plugins.commands.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public interface ICommand {
    ItemStack getIcon();

    default ISubCommand[] getSubCommands() {
        return new ISubCommand[0];
    }

    default void onCommand(Command command, CommandSender sender, String label) {}
}
