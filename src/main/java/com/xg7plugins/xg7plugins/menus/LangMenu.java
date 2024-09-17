package com.xg7plugins.xg7plugins.menus;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7menus.Slot;
import com.xg7plugins.xg7plugins.libs.xg7menus.XSeries.XMaterial;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.item.ItemBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.builders.menu.MenuBuilder;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.ItemsPageMenu;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.gui.Menu;
import com.xg7plugins.xg7plugins.libs.xg7menus.menus.player.PlayerMenu;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LangMenu {

    private ItemsPageMenu menu;
    private Player player;


    public LangMenu(Player player) {

        this.player = player;

        XG7Plugins plugin = XG7Plugins.getInstance();

        List<ItemBuilder> items = new ArrayList<>();

        XG7Plugins.getInstance().getLangManager().getLangs().asMap().forEach((s, c)-> {

            ItemBuilder builder = ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseItem(), plugin);
            builder.name(c.getString("formated-name") != null ? c.getString("formated-name") : s);
            builder.lore(Collections.singletonList(plugin.getLangManager().getPath(player, "lang-menu.item-click")));
            builder.click(event -> {
                plugin.getDatabaseManager().executeUpdate(plugin, "UPDATE FROM langentity SET langid = ? WHERE playeruuid = ?", s,player.getUniqueId());
                reload();
                Text.format("lang:[lang-menu.toggle-success]", plugin).send(player);
            });

            items.add(builder);
        });

        this.menu = MenuBuilder.page().
                title("lang[lang.title]")
                .rows(6)
                .setArea(Slot.of(2,2), Slot.of(5,8))
                .setItems(items)
                .setItem(45, ItemBuilder.from(Material.ARROW, plugin).name("lang:[lang-menu.go-back-item]").click(event -> menu.previousPage()))
                .setItem(49, ItemBuilder.from(Material.BARRIER, plugin).name("lang:[lang-menu.close-item]").click(event -> menu.close()))
                .setItem(53, ItemBuilder.from(Material.ARROW, plugin).name("lang:[lang-menu.go-next-item]").click(event -> menu.nextPage()))
                .build(player, plugin);

        menu.open();
    }


    public void reload() {
        menu.close();
        menu.open();
        PlayerMenu playerMenu = XG7Plugins.getInstance().getMenuManager().getPlayerMenuMap().get(player.getUniqueId());
        if (playerMenu != null) {
            playerMenu.clear();
            playerMenu.give();
        }
    }




}
