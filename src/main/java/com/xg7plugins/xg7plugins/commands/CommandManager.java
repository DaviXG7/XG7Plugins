package com.xg7plugins.api.commandsmanager;

import com.xg7plugins.api.XG7PluginManager;
import com.xg7plugins.api.Config;
import com.xg7plugins.api.commandsmanager.defaultCommands.BugsCommand;
import com.xg7plugins.api.commandsmanager.defaultCommands.SuggestCommand;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    private static final List<com.xg7plugins.api.commandsmanager.Command> plCommands = new ArrayList<>();

    public static void initDefaultCommands() {
        new CommandManager().init(new BugsCommand(), new SuggestCommand());
    }

    @SneakyThrows
    public void init(com.xg7plugins.api.commandsmanager.Command... commands) {

        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        for (com.xg7plugins.api.commandsmanager.Command command : commands) {

            if (command.getClass().getDeclaredConstructor().getAnnotation(CommandConfig.class) != null) {
                if (Config.getConfig("commands") != null) {
                    String path = command.getClass().getDeclaredConstructor().getAnnotation(CommandConfig.class).enabledIf();
                    if (!path.isEmpty()) if (Config.getString("commands", XG7PluginManager.getPlugin().getName().toLowerCase() + "." + path) == null) continue;
                }
            }

            if (command.getSubCommands().length > 1 && Arrays.stream(command.getSubCommands()).anyMatch(SubCommand::isRequired)) {
                throw new Exception("Required subcommand of" + command.getName() + " must be unique.");
            }

            plCommands.add(command);
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand pluginCommand = constructor.newInstance(command.getName(), XG7PluginManager.getPlugin());
            pluginCommand.setExecutor(this);
            pluginCommand.setUsage(command.getSyntax());
            pluginCommand.setTabCompleter(this);
            pluginCommand.setDescription(command.getDescription());
            pluginCommand.setAliases(Arrays.asList(command.getAliases()));
            commandMap.register(command.getName(), pluginCommand);

            if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
                Bukkit.getPluginManager().addPermission(new Permission(command.getPermission()));
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Bukkit.getScheduler().runTaskAsynchronously(XG7PluginManager.getPlugin(), () -> {
            com.xg7plugins.api.commandsmanager.Command command1 = plCommands.stream().filter(cmd -> cmd.getName().equals(command.getName())).findFirst().get();

            if (!commandSender.hasPermission(command1.getPermission())) {
                commandSender.sendMessage("You don't have permission to execute this command.");
                return;
            }

            try {
                if (command.getClass().getDeclaredConstructor().isAnnotationPresent(CommandConfig.class)) {
                    CommandConfig commandConfig = command.getClass().getDeclaredConstructor().getAnnotation(CommandConfig.class);

                    if (commandConfig.isOnlyPlayer() && !(commandSender instanceof Player)) {
                        commandSender.sendMessage("This command is only for players.");
                        return;
                    }
                    if (commandConfig.isOnlyInWorld() && (!(commandSender instanceof Player) && !XG7PluginManager.getWorldsEnabled().contains(((Player) commandConfig).getWorld().getName()))) {
                        commandSender.sendMessage("This can only execute this command on world.");
                        return;
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            command1.onCommand(command, commandSender, strings, s);
        });
        return true;
    }

    private void runCommand(com.xg7plugins.api.commandsmanager.Command command , Command bukkitCommand, CommandSender sender, String[] args, String label) {
        if (args.length == 0) {
            if (command.getSubCommands().length != 0) {
                if (command.getSubCommands()[0].isRequired()) {
                    sender.sendMessage("This subcommand is required.");
                    return;
                }
            }
        }
        for (SubCommand subCommand : command.getSubCommands()) {
            if (subCommand.getName().equals(args[0])) {
                runCommand(subCommand,bukkitCommand, sender, Arrays.copyOfRange(args,1,args.length), label);
                return;
            }
        }
        command.onCommand(bukkitCommand, sender, args, label);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return plCommands.stream().filter(cmd -> cmd.getName().equals(command.getName())).findFirst().get().onTabComplete(command,commandSender,strings,s);
    }
}
