package com.xg7plugins.utils.reflection.nms;

import com.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;

@Getter
public class Packet {

    private ReflectionObject packet;
    private PacketClass packetClass;

    public Packet(PacketClass packetClass, Object... args) {
        Class<?>[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
            if (args[i] instanceof Integer) classes[i] = int.class;
            if (args[i] instanceof Byte) classes[i] = byte.class;
            if (args[i] instanceof Boolean) classes[i] = boolean.class;
            if (args[i] instanceof Float) classes[i] = float.class;
            if (args[i] instanceof Double) classes[i] = double.class;
            if (args[i] instanceof Long) classes[i] = long.class;
            if (args[i] instanceof Short) classes[i] = short.class;
            if (args[i] instanceof Character) classes[i] = char.class;
        }
        this.packet = packetClass.getReflectionClass().getConstructor(classes).newInstance(args);
        this.packetClass = packetClass;
    }
    public Packet(PacketClass packetClass) {
        this(packetClass, new Class<?>[0], new Object[0]);
    }
    public Packet(PacketClass packetClass, Class<?>[] classes, Object... args) {
        this.packetClass = packetClass;
        this.packet = packetClass.getReflectionClass().getConstructor(classes).newInstance(args);
    }
    public Packet(Object packet) {
        if (!NMSUtil.getNMSClassViaVersion(17, "Packet", "network.protocol.Packet").isInstance(packet)) {
            throw new IllegalArgumentException("The object is not an instance of Packet");
        }
        this.packet = ReflectionObject.of(packet);

        String packetName = packet.getClass().getName().split("\\.")[packet.getClass().getName().split("\\.").length - 1];

        this.packetClass = new PacketClass(packetName);
    }

    public <T> T getField(String name) {
        return packet.getField(name);
    }

    public void setField(String name, Object value) {
        packet.setField(name, value);
    }

    public <T> T useMethod(String name, Object... args) {
        Class<?>[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }
        return packet.getMethod(name, classes).invoke(args);
    }
    public <T> T useMethod(String name, Class<?>[] classes, Object... args) {
        return packet.getMethod(name, classes).invoke(args);
    }

    public Object getPacket() {
        return packet.getObject();
    }

    public ReflectionObject getReflectionObjectPacket() {
        return packet;
    }
}
