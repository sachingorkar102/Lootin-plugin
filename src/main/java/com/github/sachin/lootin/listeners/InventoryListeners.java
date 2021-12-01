package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.gui.BarrelGui;
import com.github.sachin.lootin.gui.ChestGui;
import com.github.sachin.lootin.gui.DoubleChestGui;
import com.github.sachin.lootin.gui.GuiHolder;
import com.github.sachin.lootin.gui.MinecartGui;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListeners extends BaseListener{
    

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if(holder == null) return;
        Player player = (Player) e.getPlayer();
        if(holder instanceof DoubleChest){
            DoubleChest doubleChest = (DoubleChest) holder;
            Chest block = (Chest) doubleChest.getLeftSide();
            if(ChestUtils.isLootinContainer(null, block, ContainerType.DOUBLE_CHEST)){
                e.setCancelled(true);
                if(plugin.currentChestviewers.contains(block.getLocation())) return;
                DoubleChestGui gui = new DoubleChestGui(player,block);
                gui.open();
            }
        }
        else if(holder instanceof Chest){
            Chest block = (Chest) holder;
            if(ChestUtils.isLootinContainer(null, block, ContainerType.CHEST)){
                e.setCancelled(true);
                
                if(plugin.currentChestviewers.contains(block.getLocation())) return;
                ChestGui gui = new ChestGui(player, block);
                gui.open();
            }
        }
        else if(holder instanceof StorageMinecart){
            StorageMinecart minecart = (StorageMinecart) holder;
            if(ChestUtils.isLootinContainer(minecart, null, ContainerType.MINECART)){
                e.setCancelled(true);
                player.stopSound(Sound.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS);
                if(plugin.currentMinecartviewers.contains(minecart)) return;
                MinecartGui gui = new MinecartGui(player, minecart);
                gui.open();
            }
        }
        else if(holder instanceof Barrel){
            Barrel barrel = (Barrel) holder;
            if(ChestUtils.isLootinContainer(null, barrel, ContainerType.BARREL)){
                e.setCancelled(true);
                if(plugin.currentChestviewers.contains(barrel.getLocation())) return;
                BarrelGui gui = new BarrelGui(player, barrel);
                gui.open();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof GuiHolder){
            GuiHolder chestGui = (GuiHolder) e.getInventory().getHolder();
            chestGui.close();
        }
    }
}
