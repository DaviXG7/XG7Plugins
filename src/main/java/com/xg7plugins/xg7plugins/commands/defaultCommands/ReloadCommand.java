package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Command(
        name = "xg7pluginreload",
        descriptionPath = "commands-menu.reload",
        syntax = "/xg7pluginreload <plugin> <[config, lang, database, events, all]>",
        aliasesPath = "reload",
        perm = "xg7plugins.command.reload"
)
public class ReloadCommand implements ICommand {

    private final ISubCommand[] subCommands = new ISubCommand[]{new PluginSubCommand()};

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.commandIcon(XMaterial.STONE_BUTTON, this, XG7Plugins.getInstance()).toItemStack();
    }
    @Override
    public ISubCommand[] getSubCommands() {
        return subCommands;
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.Command command, CommandSender sender, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) suggestions.addAll(subCommands[0].getOptions());
        if (args.length == 2) suggestions = Arrays.stream(subCommands[0].getSubCommands()).map(sub -> sub.getClass().getAnnotation(SubCommand.class).name()).collect(Collectors.toList());
        return suggestions;
    }

    @SubCommand(
            name = "config",
            perm = "xg7plugins.command.reload",
            type = SubCommandType.OPTIONS
    )
    @Data
    static class PluginSubCommand implements ISubCommand {

        private Set<String> plugins = new HashSet<>();
        private final ISubCommand[] subCommands = new ISubCommand[]{new ConfigSubCommand(), new LangSubCommand(), new DatabaseSubCommand(), new EventSubCommand(), new AllSubCommand()};

        @Override
        public ISubCommand[] getSubCommands() {
            return subCommands;
        }

        @Override
        public Set<String> getOptions() {
            plugins.addAll(XG7Plugins.getInstance().getPlugins().keySet());
            plugins.add("XG7Plugins");
            return plugins;
        }

        @Override
        public ItemStack getIcon() {
            return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
        }

        @SubCommand(
                name = "config",
                perm = "xg7plugins.command.reload.config",
                type = SubCommandType.NORMAL
        )
        static class ConfigSubCommand implements ISubCommand {

            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                plugin.getConfigs().forEach(Config::reload);

                Text.format("lang:[reload-message.config]", XG7Plugins.getInstance()).send(sender);
            }
        }

        @SubCommand(
                name = "database",
                perm = "xg7plugins.command.reload.database",
                type = SubCommandType.NORMAL
        )
        static class DatabaseSubCommand implements ISubCommand {

            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                XG7Plugins.getInstance().getDatabaseManager().disconnectPlugin(plugin);
                XG7Plugins.getInstance().getDatabaseManager().connectPlugin(plugin);

                Text.format("lang:[reload-message.database]", XG7Plugins.getInstance()).send(sender);
            }
        }

        @SubCommand(
                name = "lang",
                perm = "xg7plugins.command.reload.lang",
                type = SubCommandType.NORMAL
        )
        static class LangSubCommand implements ISubCommand {

            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                plugin.getLangManager().loadAllLangs();
                Text.format("lang:[reload-message.lang]", XG7Plugins.getInstance()).send(sender);
            }
        }

        @SubCommand(
                name = "events",
                perm = "xg7plugins.command.reload.events",
                type = SubCommandType.NORMAL
        )
        static class EventSubCommand implements ISubCommand {
            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                XG7Plugins.getInstance().getEventManager().unregisterEvents(plugin);
                XG7Plugins.getInstance().getEventManager().registerPlugin(plugin);
                Text.format("lang:[reload-message.events]", XG7Plugins.getInstance()).send(sender);
            }
        }

        @SubCommand(
                name = "all",
                perm = "xg7plugins.command.reload.all",
                type = SubCommandType.NORMAL
        )
        static class AllSubCommand implements ISubCommand {

            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                Bukkit.getPluginManager().disablePlugin(plugin);
                Bukkit.getPluginManager().enablePlugin(plugin);
                Text.format("lang:[reload-message.all]", XG7Plugins.getInstance()).send(sender);
            }
        }

    }


}
