package com.xg7plugins.xg7plugins.menus;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.menu.PageMenuBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandMenu {

    private ItemsPageMenu menu;

    public CommandMenu(Plugin plugin) {

        List<ItemStack> commands = plugin.getCommands().stream().map(ICommand::getIcon).collect(Collectors.toList());

        PageMenuBuilder.page("commandMenu:" + plugin.getName());


    }



}
