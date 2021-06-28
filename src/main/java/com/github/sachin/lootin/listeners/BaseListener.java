package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.Lootin;

import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener{

    public Lootin plugin;

    public BaseListener(){
        this.plugin = Lootin.getPlugin();
    }

    
}
