package com.xg7plugins.commands.defaultCommands.reloadCommand.subcommands;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.database.DBManager;
import com.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

@Command(
        name = "database",
        description = "Reload Database Command",
        syntax = "/xg7plugins reload database (plugin)",
        permission = "xg7plugins.command.reload.database",
        isAsync = true
)
public class DatabaseSubCommand implements ICommand {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Plugin plugin = XG7Plugins.getInstance();

        if (args.len() != 0) plugin = XG7Plugins.getInstance().getPlugins().get(args.get(0, String.class));

        DBManager dbManager = XG7Plugins.getInstance().getDatabaseManager();

        dbManager.disconnectPlugin(plugin);
        dbManager.connectPlugin(plugin, plugin.loadEntites());

        Text.format("lang:[reload-message.database]", XG7Plugins.getInstance())
                .replace("[PLUGIN]", plugin.getName())
                .send(sender);
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.CHEST, this);
    }
}