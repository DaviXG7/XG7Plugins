package com.xg7plugins.xg7plugins.tasks;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    protected ScheduledExecutorService executor;

    private final Map<Plugin, HashMap<UUID, ScheduledFuture<?>>> tasksRunning = new HashMap<>();

    public TaskManager(XG7Plugins plugin) {
        Config config = plugin.getConfigsManager().getConfig("config");

        executor = (ScheduledExecutorService) Executors.newFixedThreadPool(config.get("task-threads"));

    }

    public UUID addRepeatingTask(Plugin plugin, Runnable runnable, long delay) {
        UUID taskId = UUID.randomUUID();
        tasksRunning.putIfAbsent(plugin, new HashMap<>());
        tasksRunning.get(plugin).put(taskId, executor.schedule(runnable, delay, TimeUnit.MILLISECONDS));
        return taskId;
    }
    public UUID addCooldownTask(Plugin plugin, Runnable runnable, int seconds) {
        tasksRunning.putIfAbsent(plugin, new HashMap<>());

        UUID taskId = UUID.randomUUID();

        tasksRunning.get(plugin).put(taskId, executor.schedule(runnable, seconds, TimeUnit.SECONDS));

        executor.submit(() -> {
            tasksRunning.get(plugin).get(taskId).cancel(false);
            tasksRunning.get(plugin).remove(taskId);
        });
        return taskId;
    }
    public void cancelTask(Plugin plugin, UUID id) {
        tasksRunning.get(plugin).get(id).cancel(false);
        tasksRunning.get(plugin).remove(id);
        tasksRunning.remove(plugin);
    }




}
