package com.github.sachin.lootin.compat.scheduler;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public interface Scheduler {


    void runTaskLater(Plugin plugin, Runnable task, @Nullable Location location, int delay);

    void runTaskLater(Plugin plugin, Runnable task,@Nullable Chunk chunk,int delay);
}
