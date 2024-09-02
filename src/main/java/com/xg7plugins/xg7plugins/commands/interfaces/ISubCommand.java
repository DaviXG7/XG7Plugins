package com.xg7plugins.xg7plugins.commands.interfaces;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public abstract class ISubCommand extends ICommand {

    public ISubCommand() {
        super(null);
    }

    public void onSubCommand(CommandSender sender, OfflinePlayer target, String label) {}
    public void onSubCommand(CommandSender sender, String[] args, String label) {}

    public abstract SubCommandType getType();

}
