package com.xg7plugins.xg7plugins.commands.setup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public interface ICommand {

    default boolean isEnabled() {return true;}

    default ISubCommand[] getSubCommands() {
        return new ISubCommand[0];
    }

    default void onCommand(Command command, CommandSender sender, String label) {}

    default List<String> onTabComplete(Command command, CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }

    ItemStack getIcon();
}
