package com.github.sachin.lootin.compat.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    @Override
    public Task runTaskTimer(Plugin plugin, Runnable task, long delay,long period) {
        return new PaperTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin,scheduledTask -> task.run(),delay,period));
    }


    public static class PaperTask implements Task{

        private final ScheduledTask task;

        public PaperTask(ScheduledTask task){
            this.task = task;

        }

        @Override
        public Plugin getPlugin() {
            return task.getOwningPlugin();
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
