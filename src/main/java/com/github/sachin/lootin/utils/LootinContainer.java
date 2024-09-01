package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LootinContainer {


    private final UUID containerID;
    private int closingTimer;
    private final Map<UUID, List<ItemStack>> itemMap = new HashMap<>();


    public LootinContainer(UUID containerID){
        this.containerID = containerID;
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
}
