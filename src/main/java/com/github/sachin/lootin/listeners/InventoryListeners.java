package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.gui.BarrelGui;
import com.github.sachin.lootin.gui.ChestGui;
import com.github.sachin.lootin.gui.DoubleChestGui;
import com.github.sachin.lootin.gui.GuiHolder;
import com.github.sachin.lootin.gui.MinecartGui;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListeners extends BaseListener{
    

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e){
        if(e.getAction()==Action.RIGHT_CLICK_BLOCK && e.isCancelled() && plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS)){
            Material type = e.getClickedBlock().getType();
            BlockState state = e.getClickedBlock().getState();
            if(type==Material.CHEST && ChestUtils.isLootinContainer(null, state, ContainerType.CHEST)){
                e.setCancelled(false);
            }
            else if(type==Material.BARREL && ChestUtils.isLootinContainer(null, state, ContainerType.BARREL)){
                e.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMinecartInteract(PlayerInteractEntityEvent e){
        if((e.getRightClicked() instanceof StorageMinecart) && e.isCancelled() && plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS) && ChestUtils.isLootinContainer(e.getRightClicked(), null, ContainerType.MINECART)){
            e.setCancelled(false);
        }
    }


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
