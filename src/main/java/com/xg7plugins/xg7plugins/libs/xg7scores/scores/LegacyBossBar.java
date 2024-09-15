package com.xg7plugins.xg7plugins.libs.xg7scores.scores;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
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

    private final float healthPercent;

    private final HashMap<UUID, Integer> entities = new HashMap<>();

    private static final ReflectionObject packetPlayOutEntityTeleport = NMSUtil.getNMSClass("PacketPlayOutEntityTeleport").newInstance();
    private static final ReflectionObject packetPlayOutEntityMetadata = NMSUtil.getNMSClass("PacketPlayOutEntityMetadata").newInstance();
    private static final ReflectionObject dataWatcher = NMSUtil.getNMSClass("DataWatcher").getConstructor(NMSUtil.getNMSClass("Entity").getAClass()).newInstance(NMSUtil.getNMSClass("Entity").cast(null));

    @SneakyThrows
    public LegacyBossBar(long delay, String[] text, String id, ScoreCondition condition, float healthPercent, Plugin plugin) {
        super(delay, text, id, condition, plugin);
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

            packetPlayOutEntityMetadata.setField("a", wither.getMethod("getId").invoke());
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getMethod("c").invoke());

            entities.put(player.getUniqueId(), wither.getMethod("getId").invoke());

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(packet.getObject());
            playerNMS.sendPacket(packetPlayOutEntityMetadata.getObject());
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

            packetPlayOutEntityTeleport.setField("a", entities.get(player.getUniqueId()));
            packetPlayOutEntityTeleport.setField("b", (int) (targetLocation.getX() * 32D));
            packetPlayOutEntityTeleport.setField("c", (int) (targetLocation.getY() * 32D));
            packetPlayOutEntityTeleport.setField("d", (int) (targetLocation.getZ() * 32D));
            packetPlayOutEntityTeleport.setField("e", (byte) (int) (targetLocation.getYaw() * 256F / 360F));
            packetPlayOutEntityTeleport.setField("f", (byte) (int) (targetLocation.getPitch() * 256F / 360F));

            PlayerNMS playerNMS = PlayerNMS.cast(player);

            playerNMS.sendPacket(packetPlayOutEntityTeleport);

            ReflectionMethod aMethod = dataWatcher.getMethod("a", int.class, Object.class);

            aMethod.invoke( 10, Text.format(getToUpdate()[getIndexUpdating()],plugin).getWithPlaceholders(player));
            aMethod.invoke( 2, Text.format(getToUpdate()[getIndexUpdating()],plugin).getWithPlaceholders(player));

            packetPlayOutEntityMetadata.setField("a", entities.get(player.getUniqueId()));
            packetPlayOutEntityMetadata.setField("b", dataWatcher.getMethod("c").invoke());

            playerNMS.sendPacket(packetPlayOutEntityMetadata.getObject());


        }

    }
}
