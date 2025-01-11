package com.xg7plugins.utils.location;


import lombok.Getter;
import org.bukkit.entity.Entity;

@Getter
public class Area implements Cloneable {

    private Location startPoint;
    private Location endPoint;

    public Area(Location startPoint, Location endPoint) {
        if (!startPoint.getWorldName().equals(endPoint.getWorldName())) {
            throw new IllegalArgumentException("Both locations must be in the same world");
        }
        this.startPoint = new Location(
                startPoint.getWorldName(),
                Math.min(startPoint.getX(), endPoint.getX()),
                Math.min(startPoint.getY(), endPoint.getY()),
                Math.min(startPoint.getZ(), endPoint.getZ())
        );
        this.endPoint = new Location(
                startPoint.getWorldName(),
                Math.max(startPoint.getX(), endPoint.getX()),
                Math.max(startPoint.getY(), endPoint.getY()),
                Math.max(startPoint.getZ(), endPoint.getZ())
        );
    }

    public boolean isInside(Location location) {
        return endPoint.getX() >= location.getX() && startPoint.getX() <= location.getX() &&
                endPoint.getY() >= location.getY() && startPoint.getY() <= location.getY() &&
                endPoint.getZ() >= location.getZ() && startPoint.getZ() <= location.getZ();
    }
    public boolean isInside(Entity entity) {
        return isInside(Location.fromBukkit(entity.getLocation()));
    }
    @Override
    public String toString() {
        return "Area{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                '}';
    }

    public double getVolume() {
        return Math.abs(endPoint.getX() - startPoint.getX()) * Math.abs(endPoint.getY() - startPoint.getY()) * Math.abs(endPoint.getZ() - startPoint.getZ());
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
