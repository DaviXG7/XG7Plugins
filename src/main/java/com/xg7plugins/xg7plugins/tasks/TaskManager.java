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

    public addRepeatingTask(Plugin plugin, Runnable runnable, long delay) {
        tasksRunning.putIfAbsent(plugin, new HashMap<>());
        tasksRunning.get(plugin).put(UUID.randomUUID(), executor.schedule(runnable, delay, TimeUnit.MILLISECONDS));
    }
    public addCooldownTask(Plugin plugin, Runnable runnable, int seconds) {
        tasksRunning.putIfAbsent(plugin, new HashMap<>());

        UUID taskId = UUID.randomUUID();

        tasksRunning.get(plugin).put(taskId, executor.schedule(runnable, seconds, TimeUnit.SECONDS));
        executor.submit(() -> {
            tasksRunning.get(plugin).get(taskId).cancel(false);
            tasksRunning.get(plugin).remove(taskId);
        });
    }




}
