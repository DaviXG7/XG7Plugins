package com.xg7plugins.xg7plugins.tasks;

import com.xg7plugins.xg7plugins.XG7Plugins;
import com.xg7plugins.xg7plugins.data.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManager {

    protected ExecutorService executor;

    private BukkitTask mainScheduler;

    public TaskManager(XG7Plugins plugin) {
        Config config = plugin.getConfigsManager().getConfig("config");

        executor = Executors.newFixedThreadPool(config.get("task-threads"));

        Thread thread = new Thread();

        mainScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously()

    }



}
