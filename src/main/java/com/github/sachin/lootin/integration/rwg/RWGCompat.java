package com.github.sachin.lootin.integration.rwg;

import java.util.Arrays;

import com.github.sachin.lootin.Lootin;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

import org.bukkit.plugin.Plugin;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.AddonInitializationException;

public class RWGCompat{

    public RealisticWorldGenerator api;
    private Lootin plugin;

    public RWGCompat(){
        this.api = RealisticWorldGenerator.get();
        this.plugin = Lootin.getPlugin();

    }

    public void reloadCompletions(){
        plugin.getCommandManager().getCommandCompletions().registerCompletion("rwgloottables", c -> Arrays.asList(api.getChestStorage().getNames()));
    }

    public boolean enableRwgSupport(Plugin plugin) {
        if(plugin == null) {
            return false; // No rwg plugin????
        }
        String[] version = plugin.getDescription().getVersion().split("\\.", 3);
        if(version.length < 2) {
            return false; // Even supported?
        }
        try {
            int major = Integer.parseInt(version[0]);
            int minor = Integer.parseInt(version[1]);
            if(major < 4 || (major == 4 && minor < 30)) {
                return false;
            }
        } catch(NumberFormatException exp) {
            return false;
        }
        try {
            return RealisticWorldGenerator.get().getCompatibilityManager().register(this.plugin, LootinAddon.class, this.plugin.getName());
        } catch (AddonInitializationException e) {
            this.plugin.getLogger().warning("Failed to install RealisticWorldGenerator addon");
            this.plugin.getLogger().warning(Exceptions.stackTraceToString(e));
            return false;
        }
    }
    
}
