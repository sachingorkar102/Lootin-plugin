package com.github.sachin.lootin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

import com.github.sachin.lootin.commands.Commands;
import com.github.sachin.lootin.compat.*;
import com.github.sachin.lootin.compat.rwg.RWGCompat;
import com.github.sachin.lootin.compat.scheduler.BukkitScheduler;
import com.github.sachin.lootin.compat.scheduler.PaperScheduler;
import com.github.sachin.lootin.compat.scheduler.Scheduler;
import com.github.sachin.lootin.compat.scheduler.Task;
import com.github.sachin.lootin.listeners.*;
import com.github.sachin.lootin.utils.*;
import com.github.sachin.lootin.utils.config.ConfigUpdater;
import com.github.sachin.lootin.utils.config.WorldManager;
import com.github.sachin.lootin.utils.cooldown.CooldownContainer;

import com.github.sachin.lootin.utils.storage.LootinContainer;
import com.github.sachin.lootin.utils.storage.StorageConverterUtility;
import com.github.sachin.prilib.McVersion;
import com.github.sachin.prilib.Prilib;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import me.clip.placeholderapi.PlaceholderAPI;


public final class Lootin extends JavaPlugin {

    private static Lootin plugin;

    private Prilib prilib;
    private PaperCommandManager commandManager;

    private Scheduler scheduler;
    public RWGCompat rwgCompat;
    public List<Location> currentChestviewers = new ArrayList<>();
    public List<StorageMinecart> currentMinecartviewers = new ArrayList<>();

    public Map<UUID, LootinContainer> cachedContainers = new HashMap<>();

    public Task cachedRunnable;

    public CooldownContainer interactCooldown;

    private WorldManager worldManager;

    public boolean isRunningPurpur;
    public boolean isRunningProtocolLib;

    public boolean isRunningPaper;

    public boolean isRunningBetterStructures = false;
    public boolean isRunningCustomStructures = false;

    public boolean isRunningValhallaMMO = false;
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
        try{

            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            this.isRunningPaper = true;
            getLogger().info("Running PaperMC...");
        }catch (ClassNotFoundException ignored){
            this.isRunningPaper = false;
        }

        if(isRunningPaper){
            scheduler = new PaperScheduler();
        }
        else{
            scheduler = new BukkitScheduler();
        }
        // Setup reflections
//        VersionProvider.setup();

        // Setup PlayerInteractEvent cooldown
        interactCooldown = new CooldownContainer();
        interactCooldown.setCooldown(350);
        interactCooldown.getTimer().setRunning(true);

        this.isRunningProtocolLib = getServer().getPluginManager().getPlugin("ProtocolLib") != null;
        this.commandManager = new PaperCommandManager(plugin);
        List<String> loottables = new ArrayList<>();
        for(LootTables l : LootTables.values()) loottables.add(l.getKey().toString());
        commandManager.getCommandCompletions().registerCompletion("loottables",c -> loottables);
        commandManager.registerCommand(new Commands(plugin));
        reloadConfigs();
        // register listeners
        PluginManager pm = getServer().getPluginManager();
//        if(isPost1_20_R2() && plugin.getConfig().getBoolean(LConstants.USE_NEW_LISTENER,true)){
//            getLogger().info("Registering new listener");
//            pm.registerEvents(new StructureGenerateListener(),plugin);
//        }
//        else{
//            pm.registerEvents(new ChunkLoadListener(), plugin);
//        }
//        pm.registerEvents(new StructureGenerateTempFix(),plugin);

        pm.registerEvents(new ChunkLoadListener(), plugin);
        pm.registerEvents(new InventoryListeners(), plugin);
        pm.registerEvents(new ChestEvents(), plugin);
        pm.registerEvents(new ItemFrameListener(),plugin);
        pm.registerEvents(new LootGenerateListener(),plugin);
        if(pm.isPluginEnabled("CustomStructures")){
            this.isRunningCustomStructures = true;
            getLogger().info("Found custom structures, registering listeners...");

            pm.registerEvents(new CustomStructuresListener(), plugin);
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
//        if(pm.isPluginEnabled("ValhallaMMO")){
//            this.isRunningValhallaMMO = true;
//            pm.registerEvents(new ValhallaMMOListner(),plugin);
//        }
        if(isRunningProtocolLib){
            try{
                getLogger().info("Found ProtocolLib, trying to register meta data packet listener...");
                new EntityMetaDataPacketListener();
            }catch (NoClassDefFoundError ignore) {
                isRunningProtocolLib = false;
                getLogger().severe("Error registering meta data packet listner from protocollib");
            }
        }
        if(getConfig().getBoolean("metrics",true)){
            getLogger().info("Enabling bstats...");
            new Metrics(this, 11877);
        }

        cachedRunnable = scheduler.runTaskTimer(this,() -> {
            if(!cachedContainers.isEmpty()){

                int i =0;
                Iterator<Map.Entry<UUID, LootinContainer>> iterator = cachedContainers.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<UUID, LootinContainer> entry = iterator.next();
                    LootinContainer container = entry.getValue();
                    if(container.getClosingTimer()<=0){
                        StorageConverterUtility.save(container);
                        iterator.remove();
                        i++;
                        continue;
                    }
                    container.setClosingTimer(container.getClosingTimer()-(10*20));
                }

                if(i!=0){
                    plugin.debug(i+" cached containers cleared and stored in data folder");
                }
            }
        },1,10*20);
    }



    @Override
    public void onDisable() {
        if(interactCooldown != null) {
            interactCooldown.getTimer().setRunning(false);
            interactCooldown.getTimer().kill();
            interactCooldown = null;
        }
        if(cachedRunnable != null && !cachedRunnable.isCancelled()){
            cachedRunnable.cancel();
            for(LootinContainer container : cachedContainers.values()){
                StorageConverterUtility.save(container);
            }
            cachedContainers.clear();
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

    public void sendPlayerMessage(String message,Player player){
        player.sendMessage(getMessage(message,player));
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

    public boolean isBlackListedLootable(String lootable,World world){
        List<String> list = plugin.getWorldManager().getBlackListStructures(world.getName());
        if(list.contains("ALL")) return true;
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

    public boolean isBlackListedLootable(LootTable lootable,World world){
        return isBlackListedLootable(lootable.getKey().toString(),world);
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

    public WorldManager getWorldManager() {
        if(worldManager == null) worldManager = new WorldManager();
        return worldManager;
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
        getWorldManager().saveAndReloadWorldConfigFile();
        getLogger().info("Config file reloaded");
    }

    public void debug(String message){
        if(getConfig().getBoolean(LConstants.DEBUG_MODE) && message != null){
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',message));
        }
    }

    public boolean isPost1_19(){

        return McVersion.current().isAtLeast(new McVersion(1,19));
    }

    public boolean isPost1_20_R2(){
        return McVersion.current().isAtLeast(new McVersion(1,20,2));
    }

    public boolean is1_16(){ return McVersion.current().equals(new McVersion(1,16,5));}

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public Prilib getPrilib() {
        return prilib;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

}
