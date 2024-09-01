package com.github.sachin.lootin.compat.scheduler;

import org.bukkit.plugin.Plugin;

public interface Task {

    Plugin getPlugin();
    public boolean isCancelled();
    public void cancel();
}
