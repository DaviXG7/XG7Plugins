package com.xg7plugins.xg7plugins.commands.setup;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public abstract class ISubCommand extends ICommand {

    public void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {}
    public void onSubCommand(CommandSender sender, String[] args, String label) {}
    public void onSubCommand(CommandSender sender, String[] args, String label, String optionChosed) {}

    public Set<String> getOptions() {
        return new HashSet<>();
    }
    public abstract SubCommandType getType();

}
