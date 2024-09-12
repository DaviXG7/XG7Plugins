package com.xg7plugins.xg7plugins.utils.Text;

import com.xg7plugins.xg7plugins.XG7Plugins;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextComponent {

    private static final Pattern pattern = Pattern.compile("\\[(CLICK|HOVER|CLICKHOVER)(.*?)](.*?)\\[/\\1]", Pattern.DOTALL);
    Pattern value = Pattern.compile("value=(%?[^ ]+%?)");
    Pattern textP = Pattern.compile("text=(%?[^ ]+%?)");
    Pattern src = Pattern.compile("src=(%?[^ ]+%?)");
    Pattern action = Pattern.compile("action=(%?[^ ]+%?)");


    private String text;
    private String rawText;

    public TextComponent(String text) {

        String rawText = text.replaceAll("\\[(CLICK|HOVER|CLICKHOVER)(.*?)](.*?)\\[/\\1]", "$3");

        if (rawText.startsWith("[CENTER] ")) rawText = Text.getCentralizedText(Text.PixelsSize.CHAT.getPixels(), rawText);

        this.text = text;
        this.rawText = rawText;

        // Expressão regular para encontrar tags [CLICK], [HOVER], [CLICKHOVER] e capturar os atributos
        Pattern pattern = Pattern.compile("\\[(CLICK|HOVER|CLICKHOVER)(.*?)\\](.*?)\\[/\\1\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        List<String> parts = new ArrayList<>();
        int lastIndex = 0;

        // Separar a string entre as tags e capturar os atributos
        while (matcher.find()) {
            // Adicionar o texto antes da tag
            if (lastIndex < matcher.start()) {
                parts.add(text.substring(lastIndex, matcher.start()));
            }

            // Adicionar a tag completa
            String fullTag = matcher.group(0);
            parts.add(fullTag);

            // Capturar os atributos dentro da tag
            String tagName = matcher.group(1);
            String attributes = matcher.group(2).trim();
            String content = matcher.group(3).trim();

            System.out.println("Tag: " + tagName);
            System.out.println("Conteúdo: " + content);

            // Expressão regular para capturar os atributos no formato chave=valor
            Pattern attrPattern = Pattern.compile("(\\w+)=(%?[^ ]+%?)");
            Matcher attrMatcher = attrPattern.matcher(attributes);

            System.out.println("Atributos:");
            while (attrMatcher.find()) {
                String attrName = attrMatcher.group(1);
                String attrValue = attrMatcher.group(2);
                System.out.println("  " + attrName + ": " + attrValue);
            }

            // Atualizar o índice para o próximo segmento
            lastIndex = matcher.end();
        }

        // Adicionar o texto restante após a última tag
        if (lastIndex < text.length()) {
            parts.add(text.substring(lastIndex));
        }

        System.out.println("\nSeparado em partes:");
        for (String part : parts) {
            System.out.println("\"" + part + "\"");
        }

        // Remover as tags para obter o texto sem elas
        String textWithoutTags = text.replaceAll("\\[(CLICK|HOVER|CLICKHOVER)(.*?)\\](.*?)\\[/\\1\\]", "$3");

        System.out.println("\nTexto sem as tags:");
        System.out.println(textWithoutTags.trim());


    }

    public void send(Player player) {
        if (XG7Plugins.getMinecraftVersion() < 8) {
            player.sendMessage(rawText);
            return;
        }

        Matcher matcher = pattern.matcher(text);

        List<String> parts = new ArrayList<>();
        int lastIndex = 0;

        ComponentBuilder builder = new ComponentBuilder();

        while (matcher.find()) {

            if (lastIndex < matcher.start()) {

                String noTagText = text.substring(lastIndex, matcher.start());
                builder.append(noTagText);

                parts.add(noTagText);
            }


            String fullTag = matcher.group(0);
            parts.add(fullTag);

            String tagName = matcher.group(1);
            String attributes = matcher.group(2).trim();
            String content = matcher.group(3).trim();

            net.md_5.bungee.api.chat.TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent(content);

            switch (tagName) {
                case "CLICK":
                    textComponent.setClickEvent(textP.set);
            }



            Pattern attrPattern = Pattern.compile("(\\w+)=(%?[^ ]+%?)");
            Matcher attrMatcher = attrPattern.matcher(attributes);

            System.out.println("Atributos:");
            while (attrMatcher.find()) {
                String attrName = attrMatcher.group(1);
                String attrValue = attrMatcher.group(2);
                System.out.println("  " + attrName + ": " + attrValue);
            }

            // Atualizar o índice para o próximo segmento
            lastIndex = matcher.end();
        }

    }


}
