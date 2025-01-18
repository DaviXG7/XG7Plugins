package com.xg7plugins.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class Location implements Cloneable {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location(String world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    public String getWorldName() {
        return world;
    }

    public Location add(double x, double y, double z) {
        return new Location(world, this.x + x, this.y + y, this.z + z);
    }
    public Location add(double x, double y, double z,float yaw, float pitch) {
        return new Location(world, this.x + x, this.y + y, this.z + z, this.yaw + yaw, this.pitch + pitch);
    }
    public Location add(Location locationToAdd) {
        return new Location(world, this.x + locationToAdd.getX(), this.y + locationToAdd.getY(), this.z + locationToAdd.getZ(), this.yaw + locationToAdd.getYaw(), this.pitch + locationToAdd.getPitch());
    }
    public Location subtract(double x, double y, double z) {
        return new Location(world, this.x - x, this.y - y, this.z - z);
    }
    public Location subtract(double x, double y, double z, float yaw, float pitch) {
        return new Location(world, this.x - x, this.y - y, this.z - z, this.yaw - yaw, this.pitch - pitch);
    }
    public Location subtract(Location locationToSubtract) {
        return new Location(world, this.x - locationToSubtract.getX(), this.y - locationToSubtract.getY(), this.z - locationToSubtract.getZ(), this.yaw - locationToSubtract.getYaw(), this.pitch - locationToSubtract.getPitch());
    }

    public boolean isNearby(Location location, double distance) {
        return Math.abs(location.getX() - x) <= distance && Math.abs(location.getY() - y) <= distance && Math.abs(location.getZ() - z) <= distance;
    }

    public static boolean isInside(Location location1, Location location2, Location location) {

        Location maxLocation = new Location(location1.getWorld().getName(), Math.max(location1.getX(), location2.getX()), Math.max(location1.getY(), location2.getY()), Math.max(location1.getZ(), location2.getZ()));
        Location minLocation = new Location(location1.getWorld().getName(), Math.min(location1.getX(), location2.getX()), Math.min(location1.getY(), location2.getY()), Math.min(location1.getZ(), location2.getZ()));

        return maxLocation.getX() >= location.getX() && minLocation.getX() <= location.getX() &&
                maxLocation.getY() >= location.getY() && minLocation.getY() <= location.getY() &&
                maxLocation.getZ() >= location.getZ() && minLocation.getZ() <= location.getZ();
    }


    public static Location of(String world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }
    public static Location of(String world, double x, double y, double z, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }
    public static Location fromBukkit(org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    public static Location fromPlayer(Player player) {
        return fromBukkit(player.getLocation());
    }
    public org.bukkit.Location getBukkitLocation() {
        return new org.bukkit.Location(getWorld(), x, y, z, yaw, pitch);
    }
    public com.github.retrooper.packetevents.protocol.world.Location getProtocolLocation() {
        return new com.github.retrooper.packetevents.protocol.world.Location(x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "Location{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private Location() {}
}
