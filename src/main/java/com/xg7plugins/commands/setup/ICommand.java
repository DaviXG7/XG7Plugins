package com.xg7plugins.commands.setup;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface ICommand {

    Plugin getPlugin();

    default boolean isEnabled() {return true;}

    default ICommand[] getSubCommands() {
        return new ICommand[0];
    }

    default void onCommand(CommandSender sender, CommandArgs args) {
        syntaxError(sender,this.getClass().getAnnotation(Command.class).syntax());
    }

    default List<String> onTabComplete (CommandSender sender, CommandArgs args) {
        return Collections.emptyList();
    }

    Item getIcon();

    default void syntaxError(CommandSender sender, String syntax) {
        Text.fromLang(sender, XG7Plugins.getInstance(),"commands.syntax-error")
                .thenAccept(text -> text.replace("syntax", syntax).send(sender));
    }
}
