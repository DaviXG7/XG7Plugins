package com.xg7plugins.xg7plugins.commands;

import com.xg7plugins.xg7plugins.Plugin;
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

import java.util.Arrays;
import java.util.HashMap;

public class CommandManager implements CommandExecutor {

    private static final HashMap<String, ICommand> commands = new HashMap<>();


    public static void registerCommands(Plugin plugin) {

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
                    .newInstance(commandSetup.name(), plugin.getPlugin())
                    .getObject();

            pluginCommand.setExecutor(XG7Plugins.getCommandManager());
            pluginCommand.setAliases(Arrays.asList(commandSetup.aliases()));
            pluginCommand.setDescription(commandSetup.description());
            pluginCommand.setUsage(commandSetup.syntax());
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

            ISubCommand[] subCommands = command.getSubCommands();

            if (strings.length != 0) {
                for (ISubCommand subCommand : subCommands) {
                    switch (subCommand.getType()) {
                        case NORMAL:
                                SubCommandConfig subCommandConfig = ReflectionMethod.of(subCommand, "onSubCommand", CommandSender.class, String[].class, String.class).getAnnotation(SubCommandConfig.class);

                                if (subCommandConfig == null) {
                                    Log.severe(plugin, "Normal subcommands must be annotated with @SubCommandConfig to setup the subcommand!!");
                                    continue;
                                }

                                if (!subCommandConfig.name().equalsIgnoreCase(strings[0])) {
                                    continue;
                                }

                                if (!commandSender.hasPermission(subCommandConfig.perm())) {
                                    //Fazer coisa de pegar na config permissão
                                    return;
                                }
                                if (subCommandConfig.isOnlyPlayer() && !(commandSender instanceof Player)) {
                                    //Fazer coisa pra pegar na config player
                                    return;
                                }
                                if (commandSender instanceof Player) {
                                    if (!subCommandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName()) && !plugin.getEnabledWorlds().contains("all")) {
                                        //Mensagem de não no mundo
                                        return;
                                    }
                                }

                                subCommand.onSubCommand(commandSender,strings,s);
                                return;
                        case PLAYER:
                            SubCommandConfig subCommandConfig1 = ReflectionMethod.of(subCommand, "onSubCommand", CommandSender.class, OfflinePlayer.class, String.class).getAnnotation(SubCommandConfig.class);

                            if (subCommandConfig1 == null) {
                                Log.severe(plugin, "Subcommands must be annotated with @SubCommandConfig to setup the subcommand!!");
                                continue;
                            }

                            if (!commandSender.hasPermission(subCommandConfig1.perm())) {
                                //Fazer coisa de pegar na config permissão
                                return;
                            }
                            if (subCommandConfig1.isOnlyPlayer() && !(commandSender instanceof Player)) {
                                //Fazer coisa pra pegar na config player
                                return;
                            }
                            if (commandSender instanceof Player) {
                                if (!subCommandConfig1.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName()) && !plugin.getEnabledWorlds().contains("all")) {
                                    //Mensagem de não no mundo
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(strings[0]);

                            subCommand.onSubCommand(commandSender,player,s);
                            return;
                    }
                }
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
                        if (!commandConfig.isOnlyInWorld() && plugin.getEnabledWorlds().contains(((Player) commandSender).getWorld().getName()) && !plugin.getEnabledWorlds().contains("all")) {
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
}
