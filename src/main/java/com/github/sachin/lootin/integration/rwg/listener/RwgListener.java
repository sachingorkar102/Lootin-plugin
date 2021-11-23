package com.github.sachin.lootin.integration.rwg.listener;

import com.github.sachin.lootin.integration.rwg.util.inventory.RwgInventory;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class RwgListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        ItemStack item = e.getItemInHand();
        if(item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(LConstants.RWG_IDENTITY_KEY, PersistentDataType.BYTE) && e.getBlockPlaced().getState() instanceof TileState){
            TileState state = (TileState) e.getBlockPlaced().getState();
            if(state instanceof Container && item.getItemMeta().getPersistentDataContainer().has(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)) {
                state.getPersistentDataContainer().set(LConstants.RWG_LOOTTABLE_KEY,PersistentDataType.STRING, item.getItemMeta().getPersistentDataContainer().get(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING));
            }
            state.getPersistentDataContainer().set(LConstants.RWG_IDENTITY_KEY,PersistentDataType.BYTE, (byte) 0);
            state.update();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof RwgInventory)) {
            return;
        }
        Inventory target = event.getClickedInventory();
        RwgInventory rwgInventory = (RwgInventory) inventory.getHolder();
        switch(event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                event.setCancelled(true);
                if(target != inventory) {
                    return;
                }
                select(event.getWhoClicked().getInventory(), rwgInventory, event.getSlot());
                return;
            case HOTBAR_SWAP:
            case HOTBAR_MOVE_AND_READD:
            case PICKUP_HALF:
            case PICKUP_ALL:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT:
                if(target != inventory) {
                    return;
                }
                event.setCancelled(true);
                select(event.getWhoClicked().getInventory(), rwgInventory, event.getSlot());
                return;
            case CLONE_STACK:
                return;
            default:
                event.setCancelled(true);
                return;
        }
    }

    private void select(Inventory inventory, RwgInventory rwgInventory, int slot) {
        if(rwgInventory.isType()) {
            if(slot == 40) {
                rwgInventory.selectItem(-1);
                rwgInventory.populate();
                return;
            }
            rwgInventory.giveItem(inventory, slot);
            return;
        }
        if(slot >= 27) {
            if(slot == 37 && rwgInventory.hasPrevious()) {
                rwgInventory.select(rwgInventory.getPage() - 1);
                rwgInventory.populate();
            }
            if(slot == 43 && rwgInventory.hasNext()) {
                rwgInventory.select(rwgInventory.getPage() + 1);
                rwgInventory.populate();
            }
            return;
        }
        rwgInventory.selectItem(slot);
        rwgInventory.populate();
    }
    
}
