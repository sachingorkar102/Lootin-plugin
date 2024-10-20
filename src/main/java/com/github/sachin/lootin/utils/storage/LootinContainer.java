package com.github.sachin.lootin.utils.storage;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootinContainer{


    private final UUID containerID;
    private int closingTimer;
    private String loottable;
    private List<ItemStack> defaultLoot = new ArrayList<>();

    private final Map<UUID, PlayerLootData> playerDataMap = new HashMap<>();


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

    public Map<UUID, PlayerLootData> getPlayerDataMap() {
        return playerDataMap;
    }
}
