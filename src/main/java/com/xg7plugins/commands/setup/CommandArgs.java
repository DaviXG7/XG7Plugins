package com.xg7plugins.commands.setup;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.utils.Parser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

@Getter
@AllArgsConstructor
public class CommandArgs {

    private final String[] args;

    public int len() {
        return args.length;
    }

    @SneakyThrows
    public <T> T get(int index, Class<T> type) {

        if (index >= args.length) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds!");
        }

        if (OfflinePlayer.class.isAssignableFrom(type)) return (T) Bukkit.getOfflinePlayer(args[index]);
        if (World.class.isAssignableFrom(type)) return (T) Bukkit.getWorld(args[index]);
        if (Plugin.class.isAssignableFrom(type)) return (T) XG7Plugins.getInstance().getPlugins().get(args[index]);

        if (type == Integer.class || type == int.class) return Parser.INTEGER.convert(args[index]);
        if (type == String.class) return Parser.STRING.convert(args[index]);
        if (type == Boolean.class || type == boolean.class) return Parser.BOOLEAN.convert(args[index]);
        if (type == Long.class || type == long.class) return Parser.LONG.convert(args[index]);
        if (type == Double.class || type == double.class) return Parser.DOUBLE.convert(args[index]);
        if (type == Float.class || type == float.class) return Parser.FLOAT.convert(args[index]);
        if (type == Short.class || type == short.class) return Parser.SHORT.convert(args[index]);
        if (type == Byte.class || type == byte.class) return Parser.BYTE.convert(args[index]);
        if (type == Character.class || type == char.class) return Parser.CHAR.convert(args[index]);

        return null;
    }

    @Override
    public String toString() {
        return String.join(" ", args);
    }
}
