package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.compat.ValhallaMMOListner;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import com.github.sachin.lootin.utils.LootinGui;

import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public class InventoryListeners extends BaseListener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = e.getPlayer();
        BlockState state = e.getClickedBlock().getState();;
//        ContainerType containerType = ChestUtils.getContainerType((L))
        if ((e.useInteractedBlock() == PlayerInteractEvent.Result.DENY && !plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS))) {
            return;
        }
        if(state instanceof Lootable && ChestUtils.getContainerType((Lootable)state) != null){
            Lootable lootable = (Lootable) state;
            ContainerType containerType = ChestUtils.getContainerType(lootable);
//            if(plugin.isRunningValhallaMMO && ValhallaMMOListner.firstTimerChests.contains(state.getLocation())){
//                ValhallaMMOListner.firstTimerChests.remove(state.getLocation());
//                return;
//            }
            boolean denyInteraction = ChestUtils.openLootinInventory((Lootable) state,player,state.getLocation(),null);
            if(denyInteraction) e.setUseInteractedBlock(PlayerInteractEvent.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMinecartInteract(PlayerInteractEntityEvent e) {
        if(plugin.isBlackListWorld(e.getPlayer().getWorld())) return;
        if (!(e.getRightClicked() instanceof StorageMinecart)) {
            return;
        }
        StorageMinecart minecart = (StorageMinecart) e.getRightClicked();
        if (e.isCancelled() && !plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS)) {
            return;
        }
        boolean denyInteraction = ChestUtils.openLootinInventory(minecart,e.getPlayer(),minecart.getLocation(),null);
        if(denyInteraction) e.setCancelled(true);
    }



    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e){
        if(e.getInventory().getHolder() instanceof LootinGui){
            ((LootinGui)e.getInventory().getHolder()).handleClickEvents(e);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e){
        if(e.getInventory().getHolder() instanceof LootinGui){
            ((LootinGui)e.getInventory().getHolder()).handleDragEvents(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof LootinGui) {
            ((LootinGui) e.getInventory().getHolder()).close();
        }
    }
}
