package com.github.sachin.lootin.utils.storage;

import com.github.sachin.lootin.Lootin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerLootData {

    private UUID playerID;
    private long lastLootTime = 0;
    private int refills = 0;
    private List<ItemStack> items;

    public PlayerLootData(UUID playerID){
        this.playerID = playerID;
    }

    public PlayerLootData(UUID playerID, List<ItemStack> items, long lastLootTime, int refills) {
        this.lastLootTime = lastLootTime;
        this.refills = refills;
        this.items = items;
        this.playerID = playerID;
    }

    public boolean isRefillRequired(long currentTime, World world){
        if(!Lootin.getPlugin().getWorldManager().isAutoReplenishEnabled(world.getName())) return false;
        Player player = Bukkit.getPlayer(playerID);
        if(player != null && !player.hasPermission("lootin.autoreplenish")) return false;
        long refillTime = Lootin.getPlugin().getWorldManager().getRefillTime(world.getName());
        int maxRefills = Lootin.getPlugin().getWorldManager().getMaxRefills(world.getName());
        long timeLeft = (lastLootTime+refillTime) - currentTime;
        if(timeLeft<=0){
            if(maxRefills==-1) return true;
            return maxRefills > refills;
        }
        return false;

    }



    public long getLastLootTime() {
        return lastLootTime;
    }

    public int getRefills() {
        return refills;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setLastLootTime(long lastLootTime) {
        this.lastLootTime = lastLootTime;
    }

    public void setRefills(int refills) {
        this.refills = refills;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public void setPlayerID(UUID playerID) {
        this.playerID = playerID;
    }
}
