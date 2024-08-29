package com.xg7plugins.api.commandsmanager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

@Getter
public abstract class SubCommand extends Command {

    private boolean isRequired;

    public SubCommand(String name, ItemStack icon, String description, String permission, String[] aliases, SubCommand[] subCommands) {
        super(name, icon, description, permission, aliases, subCommands);
    }
    public Player getOnlinePlayer(String name) {
        return Bukkit.getPlayer(name);
    }
    public OfflinePlayer getOffilinePlayer(String name) {
        return Bukkit.getOfflinePlayer(name);
    }
    @Override
    public List<String> onTabComplete(org.bukkit.command.Command command, CommandSender sender, String[] args, String label) {
        return Collections.emptyList();
    }
}
