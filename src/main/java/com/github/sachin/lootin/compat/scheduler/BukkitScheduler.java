package com.github.sachin.lootin.compat.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;

public class BukkitScheduler implements Scheduler{


    @Override
    public void runTaskLater(Plugin plugin, Runnable task, @Nullable Location location, int delay) {
        Bukkit.getScheduler().runTaskLater(plugin,task,delay);
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable task, @Nullable Chunk chunk, int delay) {
        Bukkit.getScheduler().runTaskLater(plugin,task,delay);

    }

    @Override
    public Task runTaskTimer(Plugin plugin, Runnable task, long delay,long period) {
        return new BukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay,period));
    }


    public static class BukkitTask implements Task{

        private final org.bukkit.scheduler.BukkitTask task;

        public BukkitTask(org.bukkit.scheduler.BukkitTask task){
            this.task = task;
        }

        @Override
        public Plugin getPlugin() {
            return task.getOwner();
        }

        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
