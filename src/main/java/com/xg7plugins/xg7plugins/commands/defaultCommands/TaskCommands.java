package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.setup.Command;
import com.xg7plugins.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.menus.TaskMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(
        name = "xg7pluginstasks",
        descriptionPath = "commands-menu.reload",
        syntax = "/xg7pluginstasks <On Console:[delete, pause]> <On console: UUID>",
        aliasesPath = "tasks",
        perm = "xg7plugins.command.reload"
)
public class TaskCommands implements ICommand {
    @Override
    public ItemStack getIcon() {
        return ItemBuilder.commandIcon(XMaterial.REPEATER, this, XG7Plugins.getInstance()).toItemStack();
    }

    @Override
    public void onCommand(org.bukkit.command.Command command, CommandSender sender, String label) {
        if (!(sender instanceof  Player)) {
            syntaxError(sender,"/xg7pluginstasks <On Console:[delete, pause]> <On console: UUID>");
            return;
        }

        TaskMenu.create(((Player) sender));
    }

}
