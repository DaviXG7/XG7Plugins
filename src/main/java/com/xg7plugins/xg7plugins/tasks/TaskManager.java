package com.xg7plugins.xg7plugins.tasks;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.*;

@Getter
public class TaskManager {


    private final Map<UUID, Future<?>> tasksRunning = new HashMap<>();
    private ScheduledExecutorService executor;

    public TaskManager(XG7Plugins plugin) {
        Config config = plugin.getConfigsManager().getConfig("config");
        executor = Executors.newScheduledThreadPool(config.get("task-threads"));
    }

    public UUID addRepeatingTask(Runnable runnable, long delay) {
        UUID taskId = UUID.randomUUID();
        tasksRunning.put(taskId, executor.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.MILLISECONDS));
        return taskId;
    }
    public UUID addCooldownTask(Runnable runnable, int seconds) {
        UUID taskId = UUID.randomUUID();

        tasksRunning.put(taskId, executor.scheduleWithFixedDelay(runnable, 0, seconds, TimeUnit.SECONDS));

        executor.submit(() -> {
            tasksRunning.get(taskId).cancel(false);
            tasksRunning.remove(taskId);
        });
        return taskId;
    }



    public UUID runTask(Runnable runnable) {
        UUID taskId = UUID.randomUUID();
        tasksRunning.put(taskId, CompletableFuture.runAsync(runnable,executor));
        return taskId;
    }

    public void cancelTask(UUID id) {
        tasksRunning.get(id).cancel(false);
        tasksRunning.remove(id);
    }





}
