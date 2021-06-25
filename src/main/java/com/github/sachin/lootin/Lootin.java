package com.github.sachin.lootin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Lootin extends JavaPlugin {

    private static Lootin plugin;

    public Lootin(){
        plugin = this;
    }

    @Override
    public void onEnable() {
    }

    public static Lootin getPlugin() {
        return plugin;
    }
}
