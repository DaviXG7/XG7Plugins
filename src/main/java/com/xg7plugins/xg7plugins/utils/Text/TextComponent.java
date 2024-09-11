package com.xg7plugins.xg7plugins.utils.Text;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

@Getter
public class TextComponent {

    private Pattern clickRegex = Pattern.compile("\\[CLICK ]");

    private String rawText;

    public TextComponent(String text) {
        //Vou fazer o negocio de tirar as tags depois
        this.rawText = text;
    }


    public void send(Player player) {
        //Traduzir os componentes
        player.spigot().sendMessage(components);
    }

    public void translateComponents() {

    }


}
