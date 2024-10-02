package com.xg7plugins.xg7plugins.libs.xg7holograms.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

@AllArgsConstructor
@Getter
public class Location {

    private String world;
    private double x;
    private double y;
    private double z;

    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    public Location add(double x, double y, double z) {
        return new Location(world, this.x + x, this.y + y, this.z + z);
    }
    public static Location fromBukkit(org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

}
