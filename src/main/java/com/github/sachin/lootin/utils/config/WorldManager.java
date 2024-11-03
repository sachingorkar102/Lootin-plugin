package com.github.sachin.lootin.utils.config;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class WorldManager {

    private YamlConfiguration worldsConfiguration;
    private Map<String,WorldConfig> worldConfigMap = new HashMap<>();

    private final String DEFAULT = "default-configuration";

    public void saveAndReloadWorldConfigFile() {
        Lootin lootin = Lootin.getPlugin();
        File worldConfigFile = new File(lootin.getDataFolder(),"worlds.yml");
        if(!worldConfigFile.exists()){
            lootin.saveResource("worlds.yml",true);
        }
        this.worldsConfiguration = YamlConfiguration.loadConfiguration(worldConfigFile);
        worldConfigMap.clear();
        for(String key : worldsConfiguration.getKeys(false)){

            if(worldsConfiguration.isConfigurationSection(key)){
                ConfigurationSection subSection = worldsConfiguration.getConfigurationSection(key);
                worldConfigMap.put(key,
                        new WorldConfig(key,subSection.getBoolean("auto-replenish-enabled",false),
                                            subSection.getBoolean("replenish-custom-containers",true),
                                            subSection.getInt("max-refills",-1),
                                            parseRefillTime(subSection.getString("refill-time","10d")),
                                            subSection.getBoolean("reset-seed-on-fill",true),
                                            subSection.getStringList(LConstants.BLACK_LIST_STRUCTURES))
                );

            }
        }
    }

    public boolean isAutoReplenishEnabled(String world){
        return getConfig(world).shouldAutoReplenish;

    }

    public int getMaxRefills(String world){
        return getConfig(world).maxRefills;
    }

    public long getRefillTime(String world){
        return getConfig(world).refillTime;
    }

    public boolean shouldResetSeed(String world){
        return getConfig(world).resetSeedOnFill;
    }

    public boolean shouldRefillCustomChests(String world){
        return getConfig(world).shouldRefillCustomChests;
    }

    public List<String> getBlackListStructures(String world){return getConfig(world).blacklistStructures;}

    private WorldConfig getConfig(String world){
        return  worldConfigMap.getOrDefault(world,worldConfigMap.get(DEFAULT));
    }

    private long parseRefillTime(String s) {
        Matcher matcher = LConstants.TIME_UNITS_PATTERN.matcher(s);
        if(matcher.find()){
            long number = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);
            if(unit.equalsIgnoreCase("d")) number = number*86400000;
            else if(unit.equalsIgnoreCase("h")) number=number*3600000;
            else if(unit.equalsIgnoreCase("m")) number=number*60000;
            else number=number*1000;
            return number;
        }
        return 10*86400000;
    }
}
