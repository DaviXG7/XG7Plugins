package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.CommandExample;
import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Set;

@CommandSetup(
        name = "xg7pluginreload",
        descriptionPath = "commands-menu.reload",
        syntax = "/xg7pluginreload <plugin> <[config, lang, database, events, all]>",
        aliasesPath = "reload"
)
public class ReloadCommand extends ICommand {

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.commandIcon(XMaterial.STONE_BUTTON, this, XG7Plugins.getInstance()).toItemStack();
    }
    @Override
    public ISubCommand[] getSubCommands() {
        return new ISubCommand[]{new PluginSubCommand()};
    }

    @CommandConfig(perm = "xg7plugins.command.reload")
    public void onCommand(Command command, CommandSender sender, String label) {}

    static class XG7PluginSubCommand extends ISubCommand {
        @Override
        public SubCommandType getType() {
            return SubCommandType.OPTIONS;
        }

        @Override
        public ISubCommand[] getSubCommands() {
            return new ISubCommand[]{new PluginSubCommand.ConfigSubCommand(), new PluginSubCommand.LangSubCommand(), new PluginSubCommand.DatabaseSubCommand(), new PluginSubCommand.EventSubCommand()};
        }

        @Override
        public Set<String> getOptions() {
            return XG7Plugins.getInstance().getPlugins().keySet().;
        }

        @Override
        public ItemStack getIcon() {
            return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
        }

    }

    static class PluginSubCommand extends ISubCommand {

        @Override
        public SubCommandType getType() {
            return SubCommandType.OPTIONS;
        }

        @Override
        public ISubCommand[] getSubCommands() {
            return new ISubCommand[]{new ConfigSubCommand(), new LangSubCommand(), new DatabaseSubCommand(), new EventSubCommand()};
        }

        @Override
        public Set<String> getOptions() {
            return XG7Plugins.getInstance().getPlugins().keySet().;
        }

        @Override
        public ItemStack getIcon() {
            return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
        }

        static class ConfigSubCommand extends ISubCommand {

            @Override
            public SubCommandType getType() {
                return SubCommandType.NORMAL;
            }
            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            @SubCommand(
                    name = "config",
                    perm = "xg7plugins.command.reload.config"
            )
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                plugin.getConfigs().forEach(Config::reload);
            }
        }
        static class DatabaseSubCommand extends ISubCommand {

            @Override
            public SubCommandType getType() {
                return SubCommandType.NORMAL;
            }
            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            @SubCommand(
                    name = "database",
                    perm = "xg7plugins.command.reload.database"
            )
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                XG7Plugins.getInstance().getDatabaseManager().disconnectPlugin(plugin);
                XG7Plugins.getInstance().getDatabaseManager().connectPlugin(plugin);
            }
        }
        static class LangSubCommand extends ISubCommand {

            @Override
            public SubCommandType getType() {
                return SubCommandType.NORMAL;
            }
            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            @SubCommand(
                    name = "lang",
                    perm = "xg7plugins.command.reload.lang"
            )
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                plugin.getLangManager().loadAllLangs();
            }
        }

        static class EventSubCommand extends ISubCommand {

            @Override
            public SubCommandType getType() {
                return SubCommandType.NORMAL;
            }
            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            @SubCommand(
                    name = "events",
                    perm = "xg7plugins.command.reload.events"
            )
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                XG7Plugins.getInstance().getEventManager().unregisterEvents(plugin);
                XG7Plugins.getInstance().getEventManager().registerPlugin(plugin);
            }
        }

        static class AllSubCommand extends ISubCommand {

            @Override
            public SubCommandType getType() {
                return SubCommandType.NORMAL;
            }
            @Override
            public ItemStack getIcon() {
                return ItemBuilder.commandIcon(XMaterial.REDSTONE, this, XG7Plugins.getInstance()).toItemStack();
            }

            @Override
            @SubCommand(
                    name = "all",
                    perm = "xg7plugins.command.reload.all"
            )
            @SneakyThrows
            public void onSubCommand(CommandSender sender, String[] args, String label) {
                Plugin plugin = XG7Plugins.getInstance().getPlugins().get(args[0]);
                File file = plugin.getDataFolder();
                Bukkit.getPluginManager().disablePlugin(plugin);
                Bukkit.getPluginManager().loadPlugin(file);
            }
        }

    }


}
