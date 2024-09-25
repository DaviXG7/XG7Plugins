package com.xg7plugins.xg7plugins.tasks;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.data.config.Config;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.*;

@Getter
public class TaskManager {


    private final Map<String, ScheduledFuture<?>> tasksRunning = new HashMap<>();
    private final ScheduledExecutorService executor;

    public TaskManager(XG7Plugins plugin) {
        Config config = plugin.getConfigsManager().getConfig("config");
        executor = Executors.newScheduledThreadPool(config.get("task-threads"));
    }

    public String addRepeatingTask(String name, Runnable runnable, long delay) {
        String taskId = name + ":" + UUID.randomUUID();
        tasksRunning.put(taskId, executor.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.MILLISECONDS));
        return taskId;
    }
    public String addCooldownTask(String name,Runnable runnable, int seconds) {
        String taskId = name + ":" + UUID.randomUUID();

        tasksRunning.put(taskId, executor.scheduleWithFixedDelay(runnable, 0, seconds, TimeUnit.SECONDS));

        executor.submit(() -> {
            tasksRunning.get(taskId).cancel(false);
            tasksRunning.remove(taskId);
        });
        return taskId;
    }



    public void runTask(Runnable runnable) {
        CompletableFuture.runAsync(runnable,executor);

    }
    public void runTaskSync(Plugin pl, Runnable runnable) {
        Bukkit.getScheduler().runTask(pl,runnable);
    }

    public void cancelTask(String id) {
        tasksRunning.get(id).cancel(false);
        tasksRunning.remove(id);
    }





}
