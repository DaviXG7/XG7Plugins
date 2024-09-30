package com.xg7plugins.xg7plugins.commands.defaultCommands;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.commands.setup.*;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7scores.builder.BossBarBuilder;
import com.xg7plugins.xg7plugins.libs.xg7scores.builder.ScoreBuilder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        XG7Plugins.getInstance().getScoreManager().initTask();
        ScoreBuilder.actionBar("teste")
                .addTextUpdate("§aTeste")
                .addTextUpdate("§cTeste")
                .addTextUpdate("§eTeste")
                .addTextUpdate("§bTeste")
                .delay(200L)
                .build(XG7Plugins.getInstance());
        ScoreBuilder.XPBar("teste2")
                .addXP("1, 0.1")
                .addXP("2, 0.2")
                .addXP("3, 0.3")
                .addXP("4, 0.4")
                .delay(200L)
                .build(XG7Plugins.getInstance());
        ScoreBuilder.scoreBoard("teste3")
                .addTitleUpdate("§aTeste")
                .addTitleUpdate("§cTeste")
                .addTitleUpdate("§eTeste")
                .addTitleUpdate("§bTeste")
                .addLine("1")
                .addLine("2")
                .addLine("3")
                .addLine("4")
                .delay(200L)
                .build(XG7Plugins.getInstance());
        ScoreBuilder.tablist("teste5")
                .addHeaderLine("§aTeste")
                .addHeaderLine("§cTeste")
                .addFooterLine("A")
                .addFooterLine("A")
                .playerSuffix("MDS")
                .playerPrefix("MDS")
                .delay(200L)
                .build(XG7Plugins.getInstance());
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAA");
        ScoreBuilder.bossBar("teste4")
                .delay(200L)
                .title(Arrays.asList("Teste1", "Teste2", "Teste3", "Teste4"))
                .color(BarColor.BLUE)
                .style(BarStyle.SEGMENTED_10)
                .progress(0.5F)
                .build(XG7Plugins.getInstance());
        System.out.println("BBBBBBBBBBBBBBBBBBB");
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
