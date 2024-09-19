package com.xg7plugins.xg7plugins.menus;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.menu.MenuBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.menu.PageMenuBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LangMenu {

    private ItemsPageMenu menu;
    private final Player player;
    @Getter
    private static long cooldownToToggle = 0;


    public LangMenu(Player player) {
        this.player = player;
        XG7Plugins.getInstance().getLangManager().loadAllLangs();
            XG7Plugins plugin = XG7Plugins.getInstance();

        Config config = plugin.getConfigsManager().getConfig("config");

            List<ItemBuilder> items = new ArrayList<>();
            XG7Plugins.getInstance().getLangManager().getLangs().asMap().forEach((s, c)-> {
                ItemBuilder builder = ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseItem(), plugin);

                builder.name(c.getString("formated-name") != null ? c.getString("formated-name") : s);

                builder.lore(Collections.singletonList(c.getString("lang-menu.item-click")));

                builder.click(event -> {

                    if (cooldownToToggle >= System.currentTimeMillis()) {
                        Text.format("Você precisa esperar " + (cooldownToToggle - System.currentTimeMillis()) + " milisegundos para trocar de linguagem!",plugin).send(player);
                        return;
                    }

                    plugin.getDatabaseManager().executeUpdate(plugin, "UPDATE langentity SET langid = ? WHERE playeruuid = ?", s, player.getUniqueId()).thenAccept(r -> {
                        plugin.getLangManager().updatePlayer(player,s);
                        plugin.getPlugins().forEach((n, pl) -> pl.getLangManager().updatePlayer(player,s));

                        reload();
                        Text.format("lang:[lang-menu.toggle-success]", plugin).send(player);
                    });

                    cooldownToToggle = System.currentTimeMillis() + Text.convertToMilliseconds(plugin, config.get("cooldown-to-toggle-lang"));

                    System.out.println(cooldownToToggle);

                });

                items.add(builder);
            });

        PageMenuBuilder builder = MenuBuilder.page("lang")
                .title("lang:[lang-menu.title]")
                .rows(6)
                .setArea(Slot.of(2,2), Slot.of(5,8))
                .setItems(items)
                .setItem(49, ItemBuilder.from(Material.BARRIER, plugin).name("lang:[lang-menu.close-item]").click(event -> menu.close()));

        int langSize = XG7Plugins.getInstance().getLangManager().getLangs().asMap().size();

        if (langSize > 27) {
            builder.setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[lang-menu.go-back-item]").click(event -> menu.previousPage()));
            builder.setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[lang-menu.go-next-item]").click(event -> menu.nextPage()));
        }

        this.menu = builder.build(player, plugin);

        menu.open();

    }


    public void reload() {
        XG7Plugins.getInstance().getMenuManager().getCachedMenus().asMap().remove("lang:" + player.getUniqueId());
        new LangMenu(player);
    }

    public static void create(Player player) {
        new LangMenu(player);
    }




}
