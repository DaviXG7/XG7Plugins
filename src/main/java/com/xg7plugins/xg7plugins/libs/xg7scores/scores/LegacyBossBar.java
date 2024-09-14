package com.xg7plugins.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.xg7plugins.libs.xg7scores.ScoreCondition;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import com.xg7plugins.xg7plugins.utils.reflection.NMSUtil;
import com.xg7plugins.xg7plugins.utils.reflection.PlayerNMS;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionMethod;
import com.xg7plugins.xg7plugins.utils.reflection.ReflectionObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class LegacyBossBar extends Score {

    private float healthPercent;

    private final HashMap<UUID, Integer> entities = new HashMap<>();

    @SneakyThrows
    public LegacyBossBar(long delay, String[] text, String id, ScoreCondition condition, float healthPercent) {
        super(delay, text, id, condition);
        this.healthPercent = healthPercent;
        XG7Plugins.getInstance().getScoreManager().registerScore(this);
    }

    @SneakyThrows
    @Override
    public void addPlayer(Player player) {
        if (!super.getPlayers().contains(player)) {
            super.addPlayer(player);

            ReflectionObject wither = NMSUtil.getNMSClass("EntityWither")
                    .getConstructor(NMSUtil.getNMSClass("World").getAClass())
                    .newInstance(NMSUtil.getNMSClass("World").cast(ReflectionMethod.of(player.getWorld(), "getHandle").invoke()));

            ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutSpawnEntityLiving")
                    .getConstructor(NMSUtil.getNMSClass("EntityLiving").getAClass())
                    .newInstance(wither);

            ReflectionObject dataWatcher = NMSUtil.getNMSClass("DataWatcher")
                    .getConstructor(NMSUtil.getNMSClass("Entity").getAClass())
                    .newInstance(NMSUtil.getNMSClass("Entity").cast(null));

            ReflectionMethod aMethod = dataWatcher.getMethod("a", int.class, Object.class);

            aMethod.invoke( 6, (healthPercent * 200) / 100);

            aMethod.invoke( 10, getToUpdate()[0]);
            aMethod.invoke( 2, getToUpdate()[0]);

            aMethod.invoke(11, (byte) 1);
            aMethod.invoke(3, (byte) 1);

            aMethod.invoke(17, 0);
            aMethod.invoke(18, 0);
            aMethod.invoke(19, 0);

            aMethod.invoke(20, 1000);
            aMethod.invoke(0, (byte) (1 << 5));

            ReflectionObject packetMetaData = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata")
                    .getConstructor(int.class, dataWatcher.getObjectClass(),boolean.class)
                    .newInstance(wither.getMethod("getId").invoke(),dataWatcher.getObject(),true);

            entities.put(player.getUniqueId(), wither.getMethod("getId").invoke());

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(packet.getObject());
            playerNMS.sendPacket(packetMetaData.getObject());
        }


    }

    @SneakyThrows
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);

        ReflectionObject packet = NMSUtil.getNMSClass("PacketPlayOutEntityDestroy").
                getConstructor(int[].class).newInstance(new int[]{entities.get(player.getUniqueId())});

        PlayerNMS.cast(player).sendPacket(packet.getObject());

        entities.remove(player.getUniqueId());
    }


    @SneakyThrows
    @Override
    public void update() {

        for (Player player : super.getPlayers()) {

            Location playerLocation = player.getLocation();
            Vector direction = playerLocation.getDirection();

            Location targetLocation = playerLocation.add(direction.multiply(40));

            ReflectionObject packetTeleport = NMSUtil.getNMSClass("PacketPlayOutEntityTeleport").newInstance();

            packetTeleport.setField("a", entities.get(player.getUniqueId()));
            packetTeleport.setField("b", (int) (targetLocation.getX() * 32D));
            packetTeleport.setField("c", (int) (targetLocation.getY() * 32D));
            packetTeleport.setField("d", (int) (targetLocation.getZ() * 32D));
            packetTeleport.setField("e", (byte) (int) (targetLocation.getYaw() * 256F / 360F));
            packetTeleport.setField("f", (byte) (int) (targetLocation.getPitch() * 256F / 360F));

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(packetTeleport);

            ReflectionObject dataWatcher = NMSUtil.getNMSClass("DataWatcher")
                    .getConstructor(NMSUtil.getNMSClass("Entity").getAClass())
                    .newInstance(NMSUtil.getNMSClass("Entity").cast(null));

            ReflectionMethod aMethod = dataWatcher.getMethod("a", int.class, Object.class);

            aMethod.invoke( 10, Text.format(getToUpdate()[getIndexUpdating()]).getWithPlaceholders(player));
            aMethod.invoke( 2, Text.format(getToUpdate()[getIndexUpdating()]).getWithPlaceholders(player));

            ReflectionObject packetMetaData = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata")
                    .getConstructor(int.class, dataWatcher.getObjectClass(),boolean.class)
                    .newInstance(entities.get(player.getUniqueId()), dataWatcher.getObject(), true);

            playerNMS.sendPacket(packetMetaData.getObject());


        }

    }
}
