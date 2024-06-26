package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class LootGenerateListener extends BaseListener{

    private void setLootinChest(Chest chest,String loottable){
        chest.getPersistentDataContainer().set(LConstants.LOOTTABLE_KEY, PersistentDataType.STRING,loottable);
        chest.setLootTable(null);
        chest.update(true);
        ChestUtils.setLootinContainer(null,chest, ContainerType.CHEST);
        plugin.getScheduler().runTaskLater(plugin,() -> {
            ChestUtils.setContainerItems(null,chest,ContainerType.CHEST, Arrays.asList(chest.getBlockInventory().getContents()), LConstants.DATA_KEY_STRING);
            chest.getBlockInventory().clear();
            chest.update(true);
        },chest.getLocation(),1);
    }

//    used when loot is not generated by player, eg. using hopper to get contents of loot chest
    @EventHandler
    public void onLootGenerate(LootGenerateEvent e){
//        plugin.debug(e.getLootTable().getKey().toString());
//        plugin.debug(e.getLootTable().getKey().getKey());
//        plugin.debug(e.getLootTable().getKey().getNamespace());
//        plugin.debug("");
        if(e.getEntity() != null) return;

        if(plugin.isBlackListedLootable(e.getLootTable())) return;
        String loottable = e.getLootTable().getKey().toString();
        if(e.getInventoryHolder() instanceof DoubleChest){
            DoubleChest doubleChest = ChestUtils.getDoubleChest(((BlockState)e.getInventoryHolder()));
            Chest chestLeft = (Chest) doubleChest.getLeftSide();
            Chest chestRight = (Chest) doubleChest.getRightSide();
            setLootinChest(chestLeft,loottable);
            setLootinChest(chestRight,loottable);
        }
        else if(e.getInventoryHolder() instanceof Chest){
            Chest chest = (Chest) e.getInventoryHolder();
            setLootinChest(chest,loottable);
        }
        else if(e.getInventoryHolder() instanceof Barrel){
            Barrel barrel = (Barrel) e.getInventoryHolder();
            barrel.getPersistentDataContainer().set(LConstants.LOOTTABLE_KEY, PersistentDataType.STRING,loottable);
            barrel.setLootTable(null);
            barrel.update(true);
            ChestUtils.setLootinContainer(null,barrel, ContainerType.BARREL);
            plugin.getScheduler().runTaskLater(plugin,() -> {
                ChestUtils.setContainerItems(null,barrel,ContainerType.BARREL, Arrays.asList(barrel.getInventory().getContents()), LConstants.DATA_KEY_STRING);
                barrel.getInventory().clear();
                barrel.update(true);
            },barrel.getLocation(),1);
        }

        else if(e.getInventoryHolder() instanceof StorageMinecart){
            StorageMinecart minecart = (StorageMinecart) e.getInventoryHolder();
            minecart.getPersistentDataContainer().set(LConstants.LOOTTABLE_KEY,PersistentDataType.STRING,loottable);
            minecart.setLootTable(null);
            ChestUtils.setLootinContainer(minecart,null,ContainerType.MINECART);
            plugin.getScheduler().runTaskLater(plugin,() -> {
                ChestUtils.setContainerItems(minecart,null,ContainerType.MINECART,Arrays.asList(minecart.getInventory().getContents()),LConstants.DATA_KEY_STRING);
                minecart.getInventory().clear();
            },minecart.getLocation(),1);
        }
    }
}
