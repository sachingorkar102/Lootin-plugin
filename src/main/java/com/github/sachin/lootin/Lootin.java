package com.github.sachin.lootin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.sachin.lootin.commands.Commands;
import com.github.sachin.lootin.listeners.ChestEvents;
import com.github.sachin.lootin.listeners.ChunkLoadListener;
import com.github.sachin.lootin.listeners.InventoryListeners;
import com.github.sachin.lootin.utils.ConfigUpdater;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;


public final class Lootin extends JavaPlugin {

    private static Lootin plugin;
    private PaperCommandManager commandManager;
    public List<Location> currentChestviewers = new ArrayList<>();
    public List<StorageMinecart> currentMinecartviewers = new ArrayList<>();

    @Override
    public void onEnable() {
            
        plugin = this;
        this.commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new Commands(plugin));
        reloadConfigs();
        // register listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChunkLoadListener(), plugin);
        pm.registerEvents(new InventoryListeners(), plugin);
        pm.registerEvents(new ChestEvents(), plugin);
        
        
    }

    public static Lootin getPlugin() {
        return plugin;
    }

    public static NamespacedKey getKey(String key){
        return new NamespacedKey(plugin, key);
    }

    public String getMessage(String key){
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix")+getConfig().getString(key,""));
    }

    public String getTitle(String key){
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(key,"Error"));
    }

    public List<String> getBlackListWorlds(){
        if(getConfig().contains("black-list-worlds")){
            List<String> list = getConfig().getStringList("black-list-worlds");
            if(list != null){
                return list;
            }
        }
        return new ArrayList<>();
    }

    public List<NamespacedKey> getBlackListStructures(){
        List<String> list = new ArrayList<>();
        List<NamespacedKey> keyList = new ArrayList<>();
        try {
            list = plugin.getConfig().getStringList(LConstants.BLACK_LIST_STRUCTURES);
            if(list.isEmpty() || list == null) return keyList;
            list.forEach(s -> {
                if(LootTables.valueOf(s) != null){
                    keyList.add(LootTables.valueOf(s).getKey());
                }
            });
            return keyList;
        } catch (Exception e) {
            return keyList;
        }
    }

    public void reloadConfigs(){
        saveDefaultConfig();
        try {
            ConfigUpdater.update(plugin, "config.yml", new File(getDataFolder(), "config.yml"), new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
        getLogger().info("Config file reloaded");
    }
}
