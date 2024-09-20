package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.setup.Command;
import com.xg7plugins.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.xg7plugins.menus.LangMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(
        name = "xg7pluginlang",
        descriptionPath = "commands-menu.lang",
        syntax = "/lang",
        aliasesPath = "lang",
        perm = "xg7plugins.command.lang"
)
public class LangCommand implements ICommand {
    @Override
    public ItemStack getIcon() {
        return ItemBuilder.commandIcon(XMaterial.WRITABLE_BOOK, this, XG7Plugins.getInstance()).toItemStack();
    }


    public void onCommand(org.bukkit.command.Command command, CommandSender sender, String label) {

        Player player = (Player) sender;

        if (XG7Plugins.getInstance().getMenuManager().getCachedMenus().asMap().containsKey("lang:" + player.getUniqueId())) {
            ((Menu) XG7Plugins.getInstance().getMenuManager().getCachedMenus().asMap().get("lang:" + player.getUniqueId())).open();
            return;
        }
        LangMenu.create(player);
    }
}
