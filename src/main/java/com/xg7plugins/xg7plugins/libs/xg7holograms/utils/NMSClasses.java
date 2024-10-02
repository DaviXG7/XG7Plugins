package com.xg7plugins.xg7plugins.libs.xg7holograms.utils;

import com.xg7plugins.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionClass;
import lombok.Getter;

public enum NMSClasses {

    ENTITY_ARMOR_STAND("EntityArmorStand", "world.entity.decoration.EntityArmorStand"),
    SPAWN_ENTITY("PacketPlayOutSpawnEntity", "network.protocol.game.PacketPlayOutSpawnEntity");


    @Getter
    private final ReflectionClass nmsClass;

    NMSClasses(String older, String newer) {
        this.nmsClass = NMSUtil.getNMSClassViaVersion(17, older, newer);
    }



}
