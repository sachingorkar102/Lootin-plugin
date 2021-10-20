package com.github.sachin.lootin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.sachin.lootin.commands.Commands;
import com.github.sachin.lootin.integration.rwg.LootinAddon;
import com.github.sachin.lootin.listeners.ChestEvents;
import com.github.sachin.lootin.listeners.ChunkLoadListener;
import com.github.sachin.lootin.listeners.InventoryListeners;
import com.github.sachin.lootin.listeners.integration.CustomStructuresLootPopulateEvent;
import com.github.sachin.lootin.listeners.integration.OTDLootListener;
import com.github.sachin.lootin.utils.ConfigUpdater;
import com.github.sachin.lootin.utils.LConstants;
import com.github.sachin.lootin.utils.Metrics;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;



public final class Lootin extends JavaPlugin {

    private static Lootin plugin;
    private PaperCommandManager commandManager;
    public List<Location> currentChestviewers = new ArrayList<>();
    public List<StorageMinecart> currentMinecartviewers = new ArrayList<>();
    public boolean isRunningPurpur;

    @Override
    public void onEnable() {
            
        plugin = this;
        try {
            Class.forName("net.pl3x.purpur.event.PlayerAFKEvent");
            this.isRunningPurpur = true;
        } catch (ClassNotFoundException e) {
            this.isRunningPurpur = false;
        }
        this.commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new Commands(plugin));
        reloadConfigs();
        // register listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChunkLoadListener(), plugin);
        
        pm.registerEvents(new InventoryListeners(), plugin);
        pm.registerEvents(new ChestEvents(), plugin);
        if(pm.isPluginEnabled("CustomStructures")){
            getLogger().info("Found custom structures, registering listeners...");

            pm.registerEvents(new CustomStructuresLootPopulateEvent(), plugin);
        }
        if(pm.isPluginEnabled("Oh_the_dungeons_youll_go")){
            getLogger().info("Found OhTheDungeons, registering listeners...");
            pm.registerEvents(new OTDLootListener(), plugin);
        }
        if(pm.isPluginEnabled("Realistic_World")){
            getLogger().info("Found RealisticWorldGenerator, trying to register compatibility addon...");
            if (enableRwgSupport(pm.getPlugin("Realistic_World"))) {
                getLogger().info("RealisticWorldGenerator addon successfully registered and installed");
            } else {
                getLogger().info("No need to register RealisticWorldGenerator compatibility addon");
            }
        }
        if(getConfig().getBoolean("metrics",true)){
            getLogger().info("Enabling bstats...");
            Metrics metrics = new Metrics(this, 11877);
        }
        
    }

    private boolean enableRwgSupport(Plugin plugin) {
        if(plugin == null) {
            return false; // No rwg plugin????
        }
        String[] version = plugin.getDescription().getVersion().split("\\.", 2);
        if(version.length < 2) {
            return false; // Even supported?
        }
        try {
            int major = Integer.parseInt(version[0]);
            int minor = Integer.parseInt(version[1]);
            if(major != 4 || minor < 30) {
                return false;
            }
        } catch(NumberFormatException exp) {
            return false;
        }
        return RealisticWorldGenerator.get().getCompatibilityManager().register(this, LootinAddon.class, getName());
    }

    public static Lootin getPlugin() {
        return plugin;
    }

    public static NamespacedKey getKey(String key){
        return new NamespacedKey(plugin, key);
    }

    public String getMessage(String key,Player player){
        String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix")+getConfig().getString(key,""));
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null){
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
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

    public int getBarrelRowCount(){
        if(isRunningPurpur){
            File file = new File("purpur.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            return config.getInt("settings.blocks.barrel.rows",3)*9;
        }
        return 27;
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
