package com.xg7plugins.xg7plugins.commands.setup;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public interface ISubCommand extends ICommand {

    default void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {}
    default void onSubCommand(CommandSender sender, String[] args, String label) {}
    default void onSubCommand(CommandSender sender, String[] args, String label, String optionChosed) {}

    default Set<String> getOptions() {
        return new HashSet<>();
    }
}
