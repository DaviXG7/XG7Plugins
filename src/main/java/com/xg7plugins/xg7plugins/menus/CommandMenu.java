package com.xg7plugins.xg7plugins.menus;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.setup.Command;
import com.xg7plugins.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.menu.MenuBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.menu.PageMenuBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandMenu {


    public static void create(Plugin plugin, Player player) {


        if (XG7Plugins.getInstance().getMenuManager().cacheExistsPlayer("commands", player)) {
            ItemsPageMenu menu = (ItemsPageMenu) XG7Plugins.getInstance().getMenuManager().getMenuByPlayer("commands", player);
            menu.open();
            return;
        }

        List<ItemBuilder> commands = plugin.getCommands().stream().map(ICommand::getIcon).collect(Collectors.toList());

        PageMenuBuilder builder = MenuBuilder.page("commands")
                .title("lang:[commands-menu.title]")
                .rows(6)
                .setArea(Slot.of(2, 2), Slot.of(5, 8))
                .setItems(commands)
                .setItem(49, ItemBuilder.from(Material.BARRIER, plugin).name("lang:[close-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).close()));
        int langSize = plugin.getLangManager().getLangs().asMap().size();

        if (langSize > 24) {
            builder.setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-back-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).previousPage()));
            builder.setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-next-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).nextPage()));
        }

        builder.build(player, plugin).open();

    }

    public static void createSubCommandMenu(Plugin plugin, Player player, ICommand command) {
        Command commandSetup = command.getClass().getAnnotation(Command.class);

        if (XG7Plugins.getInstance().getMenuManager().cacheExistsPlayer("subcommands:" + commandSetup.name(), player)) {
            ItemsPageMenu menu = (ItemsPageMenu) XG7Plugins.getInstance().getMenuManager().getMenuByPlayer("subcommands:" + commandSetup.name(), player);
            menu.open();
            return;
        }

        List<ItemBuilder> commands = Arrays.stream(command.getSubCommands()).map(ICommand::getIcon).collect(Collectors.toList());
        PageMenuBuilder builder = MenuBuilder.page("subcommands:" + commandSetup.name())
                .title(commandSetup.name())
                .rows(6)
                .setArea(Slot.of(2,2), Slot.of(5,8))
                .setItems(commands)
                .setItem(49, ItemBuilder.from(Material.BARRIER, plugin).name("lang:[close-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).close()));
        int langSize = plugin.getLangManager().getLangs().asMap().size();
        if (langSize > 24) {
            builder.setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-back-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).previousPage()));
            builder.setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[go-next-item]").click(event -> ((ItemsPageMenu) event.getClickedMenu()).nextPage()));
        }
        builder.build(player, plugin).open();

    }



}
