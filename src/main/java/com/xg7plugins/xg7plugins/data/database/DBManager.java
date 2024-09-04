package com.xg7plugins.xg7plugins.data.database;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.xg7plugins.Plugin;
import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.data.config.Config;
import com.xg7plugins.xg7plugins.data.config.Configs;
import com.xg7plugins.xg7plugins.utils.Text.Text;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DBManager {

    private static final HashMap<String, Connection> connections = new HashMap<>();

    protected static ExecutorService executor;

    @Getter
    private static Cache<Object, Entity> entitiesCached;

    private static void initManager() {
        entitiesCached = Caffeine.newBuilder().expireAfterWrite(Text.convertToMilliseconds(XG7Plugins.getDefaultPlugin(), Configs.getConfig(XG7Plugins.getDefaultPlugin(), "config").get("sql.cache-expires")), TimeUnit.MILLISECONDS).build();

    }

    @SneakyThrows
    public static void connectPlugin(Plugin plugin) {

        Config pluginConfig = Configs.getConfig(plugin,"config");

        ConnectionType connectionType = ConnectionType.valueOf(((String) pluginConfig.get("sql.type")).toUpperCase());

        String host = pluginConfig.get("sql.host");
        String port = pluginConfig.get("sql.port");
        String database = pluginConfig.get("sql.database");
        String username = pluginConfig.get("sql.username");
        String password = pluginConfig.get("sql.password");

        switch (connectionType) {
            case SQLITE:

                Class.forName("org.sqlite.JDBC");
                File file = new File(plugin.getPlugin().getDataFolder(), "data.db");

                if (!file.exists()) file.createNewFile();

                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:sqlite:" + plugin.getPlugin().getDataFolder().getPath() + "/data.db"));

                return;
            case MYSQL:

                Class.forName("org.mariadb.jdbc.Driver");
                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password));

                break;
            case MARIADB:

                Class.forName("com.mysql.cj.jdbc.Driver");
                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));

                break;
        }


    }

    public static void cacheEntity(Object id, Entity entity) {
        entitiesCached.put(id, entity);
    }

    @SneakyThrows
    public static void disconnectPlugin(Plugin plugin) {
        connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());
    }

    public static CompletableFuture<Query> executeQuery(Plugin plugin, String sql, Object... args) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                Connection connection = connections.get(plugin.getName());

                PreparedStatement ps = connection.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);


                ResultSet rs = ps.executeQuery();

                List<Map<String, Object>> results = new ArrayList<>();

                while (rs.next()) {

                    Map<String, Object> map = new HashMap<>();

                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) map.put(rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));

                    results.add(map);
                }

                return new Query(results.iterator());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        },executor);
    }

    public static void executeUpdate(Plugin plugin, String sql, Object... args) {
        executor.submit(() -> {
            try {
                Connection connection = connections.get(plugin.getName());
                PreparedStatement ps = connection.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }





}
