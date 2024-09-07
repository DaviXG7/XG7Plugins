package com.xg7plugins.xg7plugins.commands;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.interfaces.*;
import com.xg7plugins.xg7plugins.utils.Log;
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
                Log.severe(plugin, "Commands must be annotated with @CommandSetup to setup the command!!");
                continue;
            }

            CommandSetup commandSetup = command.getClass().getAnnotation(CommandSetup.class);

            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(commandSetup.name(), plugin)
                    .getObject();

            pluginCommand.setExecutor(XG7Plugins.getInstance().getCommandManager());
            pluginCommand.setAliases(Arrays.asList(commandSetup.aliases()));
            pluginCommand.setDescription(commandSetup.description());
            pluginCommand.setUsage(commandSetup.syntax());
            pluginCommand.setTabCompleter(XG7Plugins.getInstance().getCommandManager());
            commandMap.register(commandSetup.name(), pluginCommand);

            commands.put(commandSetup.name(), command);

        }


    }

    @Override
    @SneakyThrows
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] strings) {

        Bukkit.getScheduler().runTaskAsynchronously(XG7Plugins.getInstance(), () -> {

            ICommand command = commands.get(cmd.getName());

            Plugin plugin = command.getPlugin();

            if (processSubCommands(commandSender, command.getSubCommands(), plugin, strings, s, 0)) return;

            if (strings.length != 0) {
                //Syntax Error
                return;
            }

            try {
                CommandConfig commandConfig = ReflectionMethod.of(command, "onCommand", cmd.getClass(), commandSender.getClass(), s.getClass()).getAnnotation(CommandConfig.class);

                if (commandConfig != null) {
                    if (!commandSender.hasPermission(commandConfig.perm())) {
                        //Fazer coisa de pegar na config permissão
                        return;
                    }
                    if (commandConfig.isOnlyPlayer() && !(commandSender instanceof Player)) {
                        //Fazer coisa pra pegar na config player
                        return;
                    }
                    if (commandSender instanceof Player) {
                        if (!commandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                            //Mensagem de não no mundo
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
    private boolean processSubCommands(CommandSender sender, ISubCommand[] subCommands, Plugin plugin, String[] args, String label, int argsIndex) {

        if (args.length != argsIndex) {
            for (ISubCommand subCommand : subCommands) {

                switch (subCommand.getType()) {
                    case NORMAL:
                        SubCommandConfig subCommandConfig = ReflectionMethod.of(subCommand, "onSubCommand", CommandSender.class, String[].class, String.class).getAnnotation(SubCommandConfig.class);

                        if (subCommandConfig == null) {
                            Log.severe(plugin, "Normal subcommands must be annotated with @SubCommandConfig to setup the subcommand!!");
                            continue;
                        }

                        if (!subCommandConfig.name().equalsIgnoreCase(args[argsIndex])) {
                            continue;
                        }
                        if (subCommand.getSubCommands().length != 0) return processSubCommands(sender, subCommand.getSubCommands(), plugin, args, label, argsIndex + 1);
                        if (!sender.hasPermission(subCommandConfig.perm())) {
                            //Fazer coisa de pegar na config permissão
                            return true;
                        }
                        if (subCommandConfig.isOnlyPlayer() && !(sender instanceof Player)) {
                            //Fazer coisa pra pegar na config player
                            return true;
                        }
                        if (sender instanceof Player) {
                            if (!subCommandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) sender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                                //Mensagem de não no mundo
                                return true;
                            }
                        }

                        subCommand.onSubCommand(sender,args,label);
                        return true;
                    case PLAYER:
                        SubCommandConfig subCommandConfig1 = ReflectionMethod.of(subCommand, "onSubCommand", CommandSender.class, OfflinePlayer.class, String.class).getAnnotation(SubCommandConfig.class);

                        if (subCommandConfig1 == null) {
                            Log.severe(plugin, "Subcommands must be annotated with @SubCommandConfig to setup the subcommand!!");
                            continue;
                        }

                        if (!sender.hasPermission(subCommandConfig1.perm())) {
                            //Fazer coisa de pegar na config permissão
                            return true;
                        }
                        if (subCommandConfig1.isOnlyPlayer() && !(sender instanceof Player)) {
                            //Fazer coisa pra pegar na config player
                            return true;
                        }
                        if (sender instanceof Player) {
                            if (!subCommandConfig1.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) sender).getWorld().getName()) && !plugin.getEnabledWorlds().isEmpty()) {
                                //Mensagem de não no mundo
                                return true;
                            }
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

                        if (!player.hasPlayedBefore()) {
                            //Player nunca jogou antes
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
