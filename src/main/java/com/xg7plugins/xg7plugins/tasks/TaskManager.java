package com.xg7plugins.xg7plugins.tasks;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;

import java.util.*;
import java.util.concurrent.*;

public class TaskManager {


    private final Map<Plugin, HashMap<UUID, ScheduledFuture<?>>> tasksRunning = new HashMap<>();

    public UUID addRepeatingTask(Plugin plugin, Runnable runnable, long delay) {
        UUID taskId = UUID.randomUUID();
        tasksRunning.putIfAbsent(plugin, new HashMap<>());
        tasksRunning.get(plugin).put(taskId, XG7Plugins.getInstance().getExecutor().scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.MILLISECONDS));
        return taskId;
    }
    public UUID addCooldownTask(Plugin plugin, Runnable runnable, int seconds) {
        tasksRunning.putIfAbsent(plugin, new HashMap<>());

        UUID taskId = UUID.randomUUID();

        tasksRunning.get(plugin).put(taskId, XG7Plugins.getInstance().getExecutor().scheduleWithFixedDelay(runnable, 0, seconds, TimeUnit.SECONDS));

        XG7Plugins.getInstance().getExecutor().submit(() -> {
            tasksRunning.get(plugin).get(taskId).cancel(false);
            tasksRunning.get(plugin).remove(taskId);
        });
        return taskId;
    }



    public UUID runTask(Plugin plugin, Runnable runnable) {
        UUID taskId = UUID.randomUUID();
        tasksRunning.putIfAbsent(plugin, new HashMap<>());
        tasksRunning.get(plugin).put(taskId, (ScheduledFuture<?>) CompletableFuture.runAsync(runnable,XG7Plugins.getInstance().getExecutor()));
        return taskId;
    }

    public void cancelTask(Plugin plugin, UUID id) {
        tasksRunning.get(plugin).get(id).cancel(false);
        tasksRunning.get(plugin).remove(id);
        tasksRunning.remove(plugin);
    }

    public void disable(Plugin plugin) {
        tasksRunning.get(plugin).values().forEach(task -> task.cancel(false));
        tasksRunning.remove(plugin);
    }




}
