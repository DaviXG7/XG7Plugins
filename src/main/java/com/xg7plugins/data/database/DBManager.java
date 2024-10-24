package com.xg7plugins.data.database;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.text.Text;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DBManager {

    private final HashMap<String, Connection> connections = new HashMap<>();
    @Getter
    private final Cache<Object, Entity> entitiesCached;


    @SneakyThrows
    public DBManager(XG7Plugins plugin) {

        Config config = plugin.getConfigsManager().getConfig("config");

        entitiesCached = Caffeine.newBuilder().expireAfterAccess(Text.convertToMilliseconds(plugin, config.get("sql.cache-expires")), TimeUnit.MILLISECONDS).build();
    }

    @SneakyThrows
    public void connectPlugin(Plugin plugin) {

        plugin.getLog().loading("Connecting database...");

        Config pluginConfig = plugin.getConfigsManager().getConfig("config");

        if (pluginConfig == null || pluginConfig.getConfigutationSection("sql") == null) {
            plugin.getLog().warn("Connection aborted!");
            return;
        }

        ConnectionType connectionType = ConnectionType.valueOf(((String) pluginConfig.get("sql.type")).toUpperCase());

        String host = pluginConfig.get("sql.host");
        String port = pluginConfig.get("sql.port");
        String database = pluginConfig.get("sql.database");
        String username = pluginConfig.get("sql.username");
        String password = pluginConfig.get("sql.password");

        plugin.getLog().loading("Connection type: " + connectionType);

        switch (connectionType) {
            case SQLITE:

                Class.forName("org.sqlite.JDBC");
                File file = new File(plugin.getDataFolder(), "data.db");

                if (!file.exists()) file.createNewFile();

                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/data.db"));


                return;
            case MARIADB:

                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password));

                break;
            case MYSQL:

                connections.put(plugin.getName(), DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));

                break;
        }

        plugin.getLog().loading("Sucessfully connected to database!");

    }

    protected void cacheEntity(Object id, Entity entity) {
        entitiesCached.put(id, entity);
    }

    @SneakyThrows
    public void disconnectPlugin(Plugin plugin) {
        plugin.getLog().loading("Disconnecting database...");
        if (connections.get(plugin.getName()) != null) connections.get(plugin.getName()).close();
        connections.remove(plugin.getName());
        plugin.getLog().loading("Disconnected database!");
    }

    public synchronized CompletableFuture<Query> executeQuery(Plugin plugin, String sql, Object... args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = connections.get(plugin.getName());

                PreparedStatement ps = connection.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);


                ResultSet rs = ps.executeQuery();

                List<Map<String, Object>> results = new ArrayList<>();

                while (rs.next()) {

                    Map<String, Object> map = new HashMap<>();

                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) map.put(rs.getMetaData().getTableName(i + 1) + "." + rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));

                    results.add(map);
                }

                return new Query(results.iterator(), this);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        },XG7Plugins.getInstance().getTaskManager().getExecutor());
    }

    public synchronized CompletableFuture<Void> executeUpdate(Plugin plugin, String sql, Object... args) {
        return CompletableFuture.runAsync(() -> {
            try {
                Connection connection = connections.get(plugin.getName());
                PreparedStatement ps = connection.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        },XG7Plugins.getInstance().getTaskManager().getExecutor());
    }





}
