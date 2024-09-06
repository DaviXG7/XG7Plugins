package com.xg7plugins.xg7plugins.commands.interfaces;

import com.xg7plugins.xg7plugins.boot.Plugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class ICommand {

    private Plugin plugin;

    public boolean isEnabled() {
        return true;
    }

    public ISubCommand[] getSubCommands() {
        return new ISubCommand[0];
    }

    public void onCommand(Command command, CommandSender sender, String label) {}

    public List<String> onTabComplete(Command command, CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }

    public abstract ItemStack getIcon();
}
