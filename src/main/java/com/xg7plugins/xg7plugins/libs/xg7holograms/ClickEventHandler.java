package com.xg7plugins.xg7plugins.libs.xg7holograms;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.events.Event;
import com.xg7plugins.xg7plugins.events.packetevents.PacketEventHandler;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import org.bukkit.entity.Player;

public class ClickEventHandler implements Event {

    @Override
    public boolean isEnabled() {
        return true;
    }


    @PacketEventHandler(packet = "PacketPlayInUseEntity")
    public Object onClick(Player player, ReflectionObject packet) {


        ClickEvent event = new ClickEvent(ClickEvent.ClickType.RIGHT_CLICK, XG7Plugins.getInstance().getHologramsManager())



        return packet;
    }
}
