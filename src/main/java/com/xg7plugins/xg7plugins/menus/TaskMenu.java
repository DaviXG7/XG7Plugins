package com.xg7plugins.xg7plugins.menus;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import org.bukkit.entity.Player;

public class TaskMenu {

    public static void create(Player player) {
        XG7Plugins plugin = XG7Plugins.getInstance();

        if (plugin.getMenuManager().cacheExistsPlayer("tasks", player)) {
            ItemsPageMenu menu = (ItemsPageMenu) XG7Plugins.getInstance().getMenuManager().getMenuByPlayer("tasks", player);
            menu.open();
            return;
        }


    }

}
