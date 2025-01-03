package com.xg7plugins.menus;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.lang.PlayerLanguage;
import com.xg7plugins.data.lang.PlayerLanguageDAO;
import com.xg7plugins.libs.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.List;

public class LangForm extends SimpleForm {

    public LangForm() {
        super("lang-form", "lang:[lang-menu.title]", XG7Plugins.getInstance(), "lang:[lang-menu.content]");
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> components = new ArrayList<>();

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(plugin).join();

        XG7Plugins.getInstance().getLangManager().getLangs().asMap().forEach((s, c)-> {

            PlayerLanguage language = XG7Plugins.getInstance().getLangManager().getPlayerLanguageDAO().get(player.getUniqueId()).join();

            boolean selected = language != null && language.getLangId().equals(s);

            String[] icon = c.getString("bedrock-icon").split(", ");

            if (icon.length == 1) {
                components.add(ButtonComponent.of(c.getString("formated-name") != null ? selected ? "§a" + c.getString("formated-name") : "§8" + c.getString("formated-name") : selected ? "§a" + s : "§8" + s));
                return;
            }
            components.add(
                    ButtonComponent.of(
                            c.getString("formated-name") != null ? selected ? "§a" + c.getString("formated-name") : "§8" + c.getString("formated-name") : selected ? "§a" + s : "§8" + s,
                            FormImage.Type.valueOf(icon[0]),
                            icon[1]
                    )
            );

        });


        return components;
    }

    @Override
    public boolean isEnabled() {
        Config config = XG7Plugins.getInstance().getConfigsManager().getConfig("config");
        return config.get("enable-langs", Boolean.class).orElse(false) && config.get("enable-lang-form", Boolean.class).orElse(false);
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {

        XG7Plugins.getInstance().getLangManager().loadLangsFrom(plugin).thenRun(() -> XG7Plugins.getInstance().getLangManager().getPlayerLanguageDAO().get(player.getUniqueId()).thenAccept(language -> {

            String lang = XG7Plugins.getInstance().getLangManager().getLangs().asMap().keySet().toArray(new String[0])[result.clickedButtonId()];
            if (language != null && language.getLangId().equals(lang)) {
                Text.formatComponent("lang:[lang-menu.already-selected]", plugin).send(player);
                return;
            }
            if (XG7Plugins.getInstance().getCooldownManager().containsPlayer("lang-change", player)) {

                double cooldownToToggle = XG7Plugins.getInstance().getCooldownManager().getReamingTime("lang-change", player);

                Text.formatComponent("lang:[lang-menu.cooldown-to-toggle]",plugin)
                        .replace("[MILLISECONDS]", String.valueOf((cooldownToToggle - System.currentTimeMillis())))
                        .replace("[SECONDS]", String.valueOf((int)((cooldownToToggle - System.currentTimeMillis()) / 1000)))
                        .replace("[MINUTES]", String.valueOf((int)((cooldownToToggle - System.currentTimeMillis()) / 60000)))
                        .replace("[HOURS]", String.valueOf((int)((cooldownToToggle - System.currentTimeMillis()) / 3600000)))
                        .send(player);
            }

            PlayerLanguageDAO dao = XG7Plugins.getInstance().getLangManager().getPlayerLanguageDAO();

            dao.update(new PlayerLanguage(player.getUniqueId(), lang)).thenAccept(r -> {
                XG7Plugins.getInstance().getLangManager().loadLangsFrom(XG7Plugins.getInstance()).join();
                Text.formatComponent("lang:[lang-menu.changed]", plugin).send(player);
                send(player);
            });

            XG7Plugins.getInstance().getCooldownManager().addCooldown(player, "lang-change", XG7Plugins.getInstance().getConfig("config").getTime("cooldown-to-toggle-lang").orElse(10000L));


        }));
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {}

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {}

}
