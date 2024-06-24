package com.github.sachin.lootin.compat.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class BukkitScheduler implements Scheduler{


    @Override
    public void runTaskLater(Plugin plugin, Runnable task, @Nullable Location location, int delay) {
        Bukkit.getScheduler().runTaskLater(plugin,task,delay);
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable task, @org.jetbrains.annotations.Nullable Chunk chunk, int delay) {
        Bukkit.getScheduler().runTaskLater(plugin,task,delay);
    }
}
