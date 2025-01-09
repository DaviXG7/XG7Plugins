package com.xg7plugins.help.guihelp;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.MainCommand;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.libs.xg7menus.events.ClickEvent;
import com.xg7plugins.libs.xg7menus.events.MenuEvent;
import com.xg7plugins.libs.xg7menus.item.Item;
import com.xg7plugins.libs.xg7menus.menus.gui.PageMenu;
import com.xg7plugins.libs.xg7menus.menus.holders.PageMenuHolder;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandMenu extends PageMenu {

    private final Map<String, ICommand> commands;
    private final CommandMenu superMenu;

    private final HelpCommandGUI guiOrigin;

    public CommandMenu(List<ICommand> commands, String customTitle, CommandMenu superMenu) {
        super(XG7Plugins.getInstance(), "command_menu" + UUID.randomUUID(), customTitle == null ? "Commands" : customTitle, 54, new Slot(2, 2), new Slot(5, 8));
        System.out.println(commands);
        this.commands = commands.stream().collect(
                Collectors.toMap(
                        command -> command.getClass().getAnnotation(Command.class).name(),
                        command -> command
                )
        );
        this.guiOrigin = plugin.getHelpCommandGUI();
        this.superMenu = superMenu;

    }

    @Override
    public List<Item> pagedItems(Player player) {

        System.out.println(commands);

        return commands
                .values()
                .stream()
                .filter(command -> !(command instanceof MainCommand))
                .map(command -> {
                    Item item = command.getIcon();
                    item.setNBTTag("command", command.getClass().getAnnotation(Command.class).name());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected List<Item> items(Player player) {
        return Arrays.asList(
                Item.from(XMaterial.ARROW).name("lang:[go-back-item]").slot(45),
                Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[go-back-to-help]").slot(48),
                Item.from(XMaterial.REDSTONE).name("lang:[go-back-to-previous-commands]").slot(50),
                Item.from(XMaterial.ARROW).name("lang:[go-next-item]").slot(53)
        );
    }

    @Override
    public <T extends MenuEvent> void onClick(T event) {
        event.setCancelled(true);
        if (!(event instanceof ClickEvent)) return;

        ClickEvent clickEvent = (ClickEvent) event;

        switch (clickEvent.getClickedSlot()) {
            case 45:
                ((PageMenuHolder) clickEvent.getInventoryHolder()).previousPage();
                return;
            case 53:
                ((PageMenuHolder) clickEvent.getInventoryHolder()).nextPage();
                return;
            case 48:
                guiOrigin.getMenu("index").open((Player) clickEvent.getWhoClicked());
                return;
            case 50:
                if (superMenu == null) return;
                superMenu.open((Player) clickEvent.getWhoClicked());
                return;
            default:
                break;
        }

        Item item = clickEvent.getClickedItem();

        if (item.isAir()) return;

        ICommand command = commands.get(item.getTag("command", String.class).orElse(null));

        if (command == null) return;

        if (command.getSubCommands().length == 0) return;

        CommandMenu commandMenu = new CommandMenu(Arrays.asList(command.getSubCommands()), "Subcommands of: " + command.getClass().getAnnotation(Command.class).name(), this);

        commandMenu.open((Player) clickEvent.getWhoClicked());



    }
}
