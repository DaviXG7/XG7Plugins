package com.xg7plugins.xg7plugins.commands.interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ISubCommand extends ICommand {

    SubCommandType getType();

    default void onSubCommand(CommandSender sender, Player target, String label) {}
    default void onSubCommand(CommandSender sender, String arg, String label) {}
    default void onSubCommand(CommandSender sender, String label) {}
}
