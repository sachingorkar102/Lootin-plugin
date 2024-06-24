package com.github.sachin.lootin.compat.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class PaperScheduler implements Scheduler{


    @Override
    public void runTaskLater(Plugin plugin, Runnable task, @Nullable Location location, int delay) {
        Bukkit.getRegionScheduler().runDelayed(plugin,location,scheduledTask -> task.run(),delay);
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable task, @org.jetbrains.annotations.Nullable Chunk chunk, int delay) {
        Bukkit.getRegionScheduler().runDelayed(plugin,chunk.getWorld(), chunk.getX(), chunk.getZ(),scheduledTask -> task.run(),delay);
    }
}
