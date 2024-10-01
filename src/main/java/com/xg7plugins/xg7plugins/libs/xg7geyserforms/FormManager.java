package com.xg7plugins.xg7plugins.libs.xg7geyserforms;

import com.xg7plugins.xg7plugins.libs.xg7geyserforms.builders.FormCreator;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;
import java.util.Map;

public class FormManager {

    private final Map<String, FormCreator> creators = new HashMap<>();

    public void registerCreator(FormCreator creator) {
        creators.put(creator.getId(), creator);
    }
    public void sendPlayerForm(String id, Player player) {

        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) return;

        FloodgateApi.getInstance().sendForm(player.getUniqueId(), creators.get(id).build(player));
    }

    public boolean contaninsForm(String id) {
        return creators.containsKey(id);
    }

}
