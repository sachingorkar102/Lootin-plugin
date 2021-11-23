package com.github.sachin.lootin.integration.rwg;

import java.util.Arrays;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.integration.rwg.listener.RwgInventoryListener;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.AddonInitializationException;
import net.sourcewriters.spigot.rwg.legacy.api.version.IConversionAccess;

public final class RWGCompat{

    private final RealisticWorldGenerator api;
    private final Lootin plugin;

    private boolean setupFailed = false;
    private ItemStack[] heads = new ItemStack[3];

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
            boolean registered = api.getCompatibilityManager().register(this.plugin, LootinAddon.class, this.plugin.getName());
            try {
                setup();
            } catch (Exception e) {
                this.plugin.getLogger().warning("Failed run RealisticWorldGenerator addon setup. The RWG support will still work but some features may not work properly.");
                setupFailed = true;
            }
            return registered;
        } catch (AddonInitializationException e) {
            this.plugin.getLogger().warning("Failed to install RealisticWorldGenerator addon");
            this.plugin.getLogger().warning(Exceptions.stackTraceToString(e));
            return false;
        }
    }
    
    public RealisticWorldGenerator getApi(){
        return api;
    }

    public boolean isSetupFailed(){
        return setupFailed;
    }

    public ItemStack[] getHeads(){
        return heads;
    }
    
    private void setup() {
        IConversionAccess access = api.getVersionAccess().getConversionAccess();
        heads[0] = access.asHeadItem("NDNjNWNlYWM0ZjViN2YzZDhlMzUxN2ViNTdkOTc3ZmM2ZGU0MTRhMmNiZTE4NDljMTYzMmRjMDhmNTJmZDgifX19");
        heads[1] = access.asHeadItem("MmZkMjUzYzRjNmQ2NmVkNjY5NGJlYzgxOGFhYzFiZTc1OTRhM2RkOGU1OTQzOGQwMWNiNzY3MzdmOTU5In19fQ==");
        heads[2] = access.asHeadItem("MThiYTVlZTg5NGUyYzcwZDI1NGYwZjExY2NhMzU2ODIyYjA5ZWI5ZTZkYzQwODExYWMxYjQ2NzFjY2E0NmIifX19");
        Bukkit.getPluginManager().registerEvents(new RwgInventoryListener(), plugin);
    }

}
