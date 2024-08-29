package com.xg7plugins.api.commandsmanager.defaultCommands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xg7plugins.api.XG7PluginManager;
import com.xg7plugins.api.commandsmanager.Command;
import com.xg7plugins.api.commandsmanager.CommandConfig;
import com.xg7plugins.api.commandsmanager.SubCommand;
import com.xg7plugins.api.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class BugsCommand extends Command {

    @CommandConfig(enabledIf = "bugreport")
    public BugsCommand() {
        super(
                XG7PluginManager.getPlugin().getName().toLowerCase() + "bugreport",
                new ItemStack(Material.ANVIL),
                "Sends a bug report to the creator!",
                XG7PluginManager.getPlugin().getName().toLowerCase() + ".bugreport",
                Config.getList("commands",XG7PluginManager.getPlugin().getName().toLowerCase() + "bugreport").toArray(new String[0]),
                new SubCommand[0]
        );
    }
    private static long cooldownToSendOtherBug = 0;
    @Override
    public void onCommand(org.bukkit.command.Command command, CommandSender sender, String[] args, String label) {
        if (System.currentTimeMillis() < cooldownToSendOtherBug) {
            sender.sendMessage("§cYou can only send one bug every 15 minutes!");
            return;
        }
        sender.sendMessage("§aYour bug will be sent, thank you for your help!");
        cooldownToSendOtherBug = System.currentTimeMillis() + 900000;
        new Thread(() -> {

            //Manda a atualização para o discord

            JsonObject embed = new JsonObject();
            embed.addProperty("title", sender.getName() + " reportou um bug");
            embed.addProperty("color", 0x00FFFF);

            JsonArray  fields = new JsonArray();
            JsonObject field1 = new JsonObject();
            field1.addProperty("name", "Versão do servidor:");
            field1.addProperty("value", "<:java:1252027840552108143> " + Bukkit.getVersion());
            field1.addProperty("inline", true);
            fields.add(field1);

            JsonObject field2 = new JsonObject();
            field2.addProperty("name", "IP do servidor:");
            field2.addProperty("value", Bukkit.getServer().getIp() + ":" + Bukkit.getServer().getPort());
            field2.addProperty("inline", true);
            fields.add(field2);

            JsonObject field3 = new JsonObject();
            field3.addProperty("name", "Conteúdo:");
            field3.addProperty("value", String.join(" ", args));
            field3.addProperty("inline", false);
            fields.add(field3);

            embed.add("fields", fields);

            JsonObject element = new JsonObject();
            element.addProperty("text", "Plugin - " + XG7PluginManager.getPlugin().getName());

            embed.add("footer",element);

            JsonArray array = new JsonArray();
            array.add(embed);

            JsonObject payload = new JsonObject();
            payload.addProperty("content", "BUG");
            payload.add("embeds", array);

            // Envia o payload ao webhook
            try {
                URL url = new URL("https://discord.com/api/webhooks/1206357532197785660/BxlaCHOAjOIG4T5UHh9RvxO5ifyENfJrnCuP84F2WTIMv9Ova2NQvkGUppK8RlMDlgkl");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.addRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStream stream = connection.getOutputStream();
                stream.write(payload.toString().getBytes());
                stream.flush();
                stream.close();

                connection.getInputStream().close();
                connection.disconnect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.Command command, CommandSender sender, String[] args, String label) {
        return Collections.emptyList();
    }

}
