package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LootinContainer implements ConfigurationSerializable {


    private final UUID containerID;
    private int closingTimer;
    private String loottable;
    private List<ItemStack> defaultLoot = new ArrayList<>();
    private final Map<UUID, List<ItemStack>> itemMap = new HashMap<>();


    public LootinContainer(UUID containerID){
        this.containerID = containerID;
        resetClosingTimer();
    }

    public LootinContainer(UUID containerID,String loottable,List<ItemStack> defaultLoot){
        this.containerID = containerID;
        this.loottable = loottable;
        this.defaultLoot = defaultLoot;
        resetClosingTimer();
    }


    public Map<UUID, List<ItemStack>> getItemMap() {
        return itemMap;
    }

    public UUID getContainerID() {
        return containerID;
    }

    public void setClosingTimer(int closingTimer) {
        this.closingTimer = closingTimer;
    }

    public int getClosingTimer() {
        return closingTimer;
    }

    public void resetClosingTimer(){
        closingTimer = Lootin.getPlugin().getConfig().getInt(LConstants.KEEP_IN_MEMORY,6000);
    }

    public String getLoottable() {
        return loottable;
    }

    public void setLoottable(String loottable) {
        this.loottable = loottable;
    }

    public List<ItemStack> getDefaultLoot() {
        return defaultLoot;
    }

    public void setDefaultLoot(List<ItemStack> defaultLoot) {
        this.defaultLoot = defaultLoot;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        for(UUID uuid : itemMap.keySet()){
            map.put(uuid.toString(),itemMap.get(uuid));
        }
        map.put("other-data.default-loot",defaultLoot);
        map.put("other-data.loottable",loottable);
        return map;
    }


    public static LootinContainer deserialize(Map<String,Object> map){

        Map<UUID,List<ItemStack>> itemMap = new HashMap<>();

        for(String s : map.keySet()){
            if(StorageConverterUtility.isValidUUID(s)){
                itemMap.put(UUID.fromString(s),(List<ItemStack>) map.get(s));
            }
            if(s.equals("other-data.default-loot")){

            }
        }
        return null;
    }

}
