package com.github.sachin.lootin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.github.sachin.lootin.commands.Commands;
import com.github.sachin.lootin.compat.WGFlag;
import com.github.sachin.lootin.compat.rwg.RWGCompat;
import com.github.sachin.lootin.listeners.*;
import com.github.sachin.lootin.compat.BetterStructuresListener;
import com.github.sachin.lootin.compat.CustomStructuresLootPopulateEvent;
import com.github.sachin.lootin.compat.OTDLootListener;
import com.github.sachin.lootin.utils.ConfigUpdater;
import com.github.sachin.lootin.utils.LConstants;
import com.github.sachin.lootin.utils.Metrics;
import com.github.sachin.lootin.utils.cooldown.CooldownContainer;

import com.github.sachin.prilib.Prilib;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.Lootable;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import me.clip.placeholderapi.PlaceholderAPI;



public final class Lootin extends JavaPlugin {

    private static Lootin plugin;

    private Prilib prilib;
    private PaperCommandManager commandManager;
    public RWGCompat rwgCompat;
    public List<Location> currentChestviewers = new ArrayList<>();
    public List<StorageMinecart> currentMinecartviewers = new ArrayList<>();

    public CooldownContainer interactCooldown;

    public boolean isRunningPurpur;
    public boolean isRunningProtocolLib;

    public boolean isRunningBetterStructures;

    public boolean isRunningWG;

    private WGFlag WGflag;

    @Override
    public void onLoad() {
        plugin = this;
        isRunningWG = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        if(isRunningWG){
            WGflag = new WGFlag();
            plugin.getLogger().info("Found WorldGuard, registering "+LConstants.WG_FLAG_NAME+" flag");
            WGflag.registerFlag();
        }
    }

    @Override
    public void onEnable() {


        prilib = new Prilib(this);
        prilib.initialize();
        if(!prilib.isNMSEnabled()){
            getLogger().severe("Running incompatible minecraft version, disabling lootin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            Class.forName("net.pl3x.purpur.event.PlayerAFKEvent");
            this.isRunningPurpur = true;
        } catch (ClassNotFoundException e) {
            this.isRunningPurpur = false;
        }
        // Setup reflections
//        VersionProvider.setup();

        // Setup PlayerInteractEvent cooldown
        interactCooldown = new CooldownContainer();
        interactCooldown.setCooldown(350);
        interactCooldown.getTimer().setRunning(true);

        this.isRunningProtocolLib = getServer().getPluginManager().getPlugin("ProtocolLib") != null;
        this.commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new Commands(plugin));
        reloadConfigs();
        // register listeners
        PluginManager pm = getServer().getPluginManager();
        if(isPost1_20_R2() && plugin.getConfig().getBoolean(LConstants.USE_NEW_LISTENER,true)){
            getLogger().info("Registering new listener");
            pm.registerEvents(new StructureGenerateListener(),plugin);
        }
        else{
            pm.registerEvents(new ChunkLoadListener(), plugin);
        }
        pm.registerEvents(new InventoryListeners(), plugin);
        pm.registerEvents(new ChestEvents(), plugin);
        pm.registerEvents(new ItemFrameListener(),plugin);
        pm.registerEvents(new LootGenerateListener(),plugin);
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
            this.rwgCompat = new RWGCompat();
            if (rwgCompat.enableRwgSupport(pm.getPlugin("Realistic_World"))) {
                rwgCompat.setup();
                getLogger().info("RealisticWorldGenerator addon successfully registered and installed");
            } else {
                getLogger().info("No need to register RealisticWorldGenerator compatibility addon");
            }
        }
        if(pm.isPluginEnabled("BetterStructures")){
            this.isRunningBetterStructures = true;
            getLogger().info("Found BetterStructures, registering listeners...");
            pm.registerEvents(new BetterStructuresListener(),plugin);
        }
        if(isRunningProtocolLib){
            getLogger().info("Found ProtocolLib, registering meta data packet listener...");
            new EntityMetaDataPacketListener();
        }
        if(getConfig().getBoolean("metrics",true)){
            getLogger().info("Enabling bstats...");
            new Metrics(this, 11877);
        }

    }

    @Override
    public void onDisable() {
        if(interactCooldown != null) {
            interactCooldown.getTimer().setRunning(false);
            interactCooldown.getTimer().kill();
            interactCooldown = null;
        }
        // Clear reflections
//        VersionProvider.PROVIDER.deleteAll();
    }
    
    public static Lootin getPlugin() {
        return plugin;
    }

    public static NamespacedKey getKey(String key){
        return new NamespacedKey(plugin, key);
    }

    public String getMessage(String key,Player player){
        String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix")+getConfig().getString(key,key));
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null){
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public String getPrefix(){
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix"));
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

    public boolean isBlackListWorld(World world){
        return getBlackListWorlds().contains(world.getName());
    }

    public boolean isBlackListWorld(UUID uuid){
        World world = getServer().getWorld(uuid);
        if(world == null){
            return false;
        }
        return getBlackListWorlds().contains(world.getName());
    }

    public boolean isBlackListedLootable(String lootable){
        List<String> list = plugin.getConfig().getStringList(LConstants.BLACK_LIST_STRUCTURES);
        for(String s : list){
            if(s.startsWith("^") && lootable.startsWith(s.replace("^", ""))){
                return true;
            }
            if(s.endsWith("$") && lootable.endsWith(s.replace("$", ""))){
                return true;
            }
            if(lootable.equals(s)) return true;

        }
        return false;
    }

    public boolean isBlackListedLootable(LootTable lootable){
        return isBlackListedLootable(lootable.getKey().toString());
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

    public WGFlag getWGflag() {
        return WGflag;
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

    public void debug(String message){
        if(getConfig().getBoolean(LConstants.DEBUG_MODE) && message != null){
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',message));
        }
    }

    public boolean isPost1_19(){
        return Arrays.asList("v1_19_R2","v1_19_R3","v1_20_R1","v1_20_R2","v1_20_R3").contains(prilib.getBukkitVersion());
    }

    public boolean isPost1_20_R2(){
        return Arrays.asList("v1_20_R2","v1_20_R3").contains(prilib.getBukkitVersion());
    }

    public boolean is1_16(){ return prilib.getBukkitVersion().equals("v1_16_R3");}

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public Prilib getPrilib() {
        return prilib;
    }
}
