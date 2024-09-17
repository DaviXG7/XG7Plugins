package com.xg7plugins.xg7plugins.commands;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionMethod;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    private final HashMap<String, ICommand> commands = new HashMap<>();

    public CommandManager(Plugin plugin) {

        this.plugin = plugin;

        CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");

        for (ICommand command : plugin.getCommands()) {

            if (!command.isEnabled()) continue;

            if (command.getClass().getAnnotation(CommandSetup.class) == null) {
                plugin.getLog().severe("Commands must be annotated with @CommandSetup to setup the command!!");
                continue;
            }

            CommandSetup commandSetup = command.getClass().getAnnotation(CommandSetup.class);

            String aliases = plugin.getConfigsManager().getConfig("commands").get(commandSetup.aliasesPath());
            if (aliases == null) break;

            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(commandSetup.name(), plugin)
                    .getObject();

            if (!aliases.isEmpty()) pluginCommand.setAliases(Arrays.asList(aliases.split(", ")));


            pluginCommand.setExecutor(this);
            pluginCommand.setDescription(plugin.getLangManager().getLang(plugin.getLangManager().getMainLang()).getString(commandSetup.descriptionPath() + ".desc"));
            pluginCommand.setUsage(commandSetup.syntax());
            pluginCommand.setTabCompleter(this);
            commandMap.register(commandSetup.name(), pluginCommand);

            commands.put(commandSetup.name(), command);

        }


    }

    @Override
    @SneakyThrows
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {

        Bukkit.getScheduler().runTaskAsynchronously(XG7Plugins.getInstance(), () -> {

            ICommand command = commands.get(cmd.getName());

            if (processSubCommands(commandSender, command.getSubCommands(), strings, s, 0)) return;

            if (strings.length != 0) {
                Text.format("lang:[commands.syntax-error]",plugin);
                return;
            }

            try {
                CommandConfig commandConfig = ReflectionMethod.of(command, "onCommand", cmd.getClass(), commandSender.getClass(), s.getClass()).getAnnotation(CommandConfig.class);

                if (commandConfig != null) {

                    if (!commandSender.hasPermission(commandConfig.perm())) {
                        Text.format("lang:[commands.no-permission]",plugin);
                        return;
                    }
                    if (commandConfig.isOnlyPlayer() && !(commandSender instanceof Player)) {
                        Text.format("lang:[commands.not-a-player]",plugin);
                        return;
                    }
                    if (commandSender instanceof Player) {
                        if (!commandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                            Text.format("lang:[commands.disabled-world]",plugin);
                            return;
                        }
                    }
                }
            } catch (Exception ignored) {}

            command.onCommand(cmd,commandSender,s);

        });

        return true;
    }

    @SuppressWarnings("deprecated")
    private boolean processSubCommands(CommandSender sender, ISubCommand[] subCommands, String[] args, String label, int argsIndex) {

        if (subCommands == null) return false;

        if (args.length != argsIndex) {
            for (ISubCommand subCommand : subCommands) {

                switch (subCommand.getType()) {
                    case NORMAL:
                        SubCommand subCommandConfig = ReflectionMethod.of(subCommand, "onSubCommand", CommandSender.class, String[].class, String.class).getAnnotation(SubCommand.class);

                        if (subCommandConfig == null) {
                            plugin.getLog().severe("Normal subcommands must be annotated with @SubCommandConfig to setup the subcommand!!");
                            continue;
                        }

                        if (!subCommandConfig.name().equalsIgnoreCase(args[argsIndex])) {
                            continue;
                        }
                        if (subCommand.getSubCommands().length != 0) return processSubCommands(sender, subCommand.getSubCommands(), args, label, argsIndex + 1);
                        if (!sender.hasPermission(subCommandConfig.perm())) {
                            Text.format("lang:[commands.no-permission]",plugin);
                            return true;
                        }
                        if (subCommandConfig.isOnlyPlayer() && !(sender instanceof Player)) {
                            Text.format("lang:[commands.not-a-player]",plugin);
                            return true;
                        }
                        if (sender instanceof Player) {
                            if (!subCommandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) sender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                                Text.format("lang:[commands.disabled-world]",plugin);
                                return true;
                            }
                        }

                        subCommand.onSubCommand(sender,args,label);
                        return true;
                    case PLAYER:
                        SubCommand subCommandConfig1 = ReflectionMethod.of(subCommand, "onSubCommand", CommandSender.class, OfflinePlayer.class, String.class).getAnnotation(SubCommand.class);

                        if (subCommandConfig1 == null) {
                            plugin.getLog().severe("Subcommands must be annotated with @SubCommandConfig to setup the subcommand!!");
                            continue;
                        }

                        if (!sender.hasPermission(subCommandConfig1.perm())) {
                            Text.format("lang:[commands.no-permission]",plugin);
                            return true;
                        }
                        if (subCommandConfig1.isOnlyPlayer() && !(sender instanceof Player)) {
                            Text.format("lang:[commands.not-a-player]",plugin);
                            return true;
                        }
                        if (sender instanceof Player) {
                            if (!subCommandConfig1.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) sender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                                Text.format("lang:[commands.disabled-world]",plugin);
                                return true;
                            }
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

                        if (!player.hasPlayedBefore()) {
                            Text.format("lang:[commands.never-played]",plugin);
                            return true;
                        }

                        subCommand.onSubCommand(sender,player,label);
                        return true;
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {
        return commands.get(cmd.getName()).onTabComplete(cmd,commandSender,s,strings);
    }
}
