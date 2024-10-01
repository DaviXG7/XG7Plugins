package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.libs.xg7geyserforms.builders.ComponentFactory;
import com.xg7plugins.xg7plugins.libs.xg7geyserforms.builders.FormCreator;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7scores.builder.BossBarBuilder;
import com.xg7plugins.xg7plugins.libs.xg7scores.builder.ScoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;

import java.util.Arrays;

@Command(
        name = "teste",
        description = "Teste",
        syntax = "/teste",
        aliasesPath = "teste",
        perm = "xg7plugins.command.teste",
        isOnlyPlayer = true
)
public class TesteCommand implements ICommand {

    @Override
    public ISubCommand[] getSubCommands() {
        return new ISubCommand[]{new Verify()};
    }

    @Override
    public void onCommand(org.bukkit.command.Command command, Player player, String label) {
        try {
            XG7Plugins.getInstance().getFormManager().registerCreator(
                    FormCreator.custom("id", XG7Plugins.getInstance())
                            .title("Teste")
                            .addComponent(ComponentFactory.dropDown("lang:[formated-name]", Arrays.asList("op1", "op2"), 0))
                            .onFinish((form, response) -> {
                                player.sendMessage("Teste");

                                CustomFormResponse response1 = (CustomFormResponse) response;
                            })
                            .onError((form, response) -> {
                                player.sendMessage("Teste");
                            })
                            .onClose(form -> {
                                player.sendMessage("Teste");
                            })
            );

            XG7Plugins.getInstance().getFormManager().sendPlayerForm("id", Bukkit.getPlayer(".DaviXG7"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public ItemBuilder getIcon() {
        return null;
    }

    @SubCommand(
            name = "verify",
            description = "Verify",
            syntax = "/teste verify",
            perm = "xg7plugins.command.teste.verify",
            type = SubCommandType.NORMAL
    )
    static class Verify implements ISubCommand {

        @Override
        public void onSubCommand(CommandSender sender, String[] args, String label) {
            XG7Plugins.getInstance().getScoreManager().getScoreboards().get("teste")
                    .getPlayers().forEach(player -> {
                        sender.sendMessage(player + "");
                    });
        }

        @Override
            public ItemBuilder getIcon() {
                return null;
            }
    }
}
