package com.github.sachin.lootin.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.ItemSerializer;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ChestEvents extends BaseListener{
    

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChestBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if(block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            if(ChestUtils.isLootinContainer(null, chest, ContainerType.CHEST)){
                if(plugin.currentChestviewers.contains(chest.getLocation())){
                    player.sendMessage(plugin.getMessage(LConstants.CHEST_EDITED));
                    e.setCancelled(true);
                    return;
                }
                if(player.hasPermission("lootin.breakchest.bypass")){
                    if(player.isSneaking()){
                        chest.getInventory().clear();

                        if(!plugin.getConfig().getBoolean(LConstants.DELETE_ITEMS_CONFIG) && ChestUtils.hasPlayerLoot(null, chest, player, ContainerType.CHEST)){
                            List<ItemStack> items = ChestUtils.getContainerItems(null, chest, ContainerType.CHEST, player);
                            if(items != null){
                                items.forEach(i -> {
                                    if(i != null){
                                        player.getWorld().dropItemNaturally(block.getLocation(), i);
                                    }
                                });
                            }
                        }
                    }
                    else{
                        player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP));
                        e.setCancelled(true);
                    }
                }
                else{
                    e.setCancelled(true);
                    player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP));
                }
            }
        }
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e){
        List<InventoryHolder> holders = Arrays.asList(e.getSource().getHolder(),e.getDestination().getHolder());
        
        for (InventoryHolder h1 : holders) {
            if(h1 == null) return;
            if(h1 instanceof DoubleChest){
                DoubleChest doubleChest = (DoubleChest) h1;
                PersistentDataContainer d1 = ((Chest)doubleChest.getLeftSide()).getPersistentDataContainer();
                PersistentDataContainer d2 = ((Chest)doubleChest.getRightSide()).getPersistentDataContainer();
                if(d1.has(LConstants.IDENTITY_KEY, PersistentDataType.STRING) || d2.has(LConstants.IDENTITY_KEY, PersistentDataType.STRING)){
                    e.setCancelled(true);
                }
            }
            if(h1 instanceof Chest){
                if(ChestUtils.isLootinContainer(null, ((Chest)h1), ContainerType.CHEST)){
                    e.setCancelled(true);
                }
            }
            else if(h1 instanceof StorageMinecart){
                if(ChestUtils.isLootinContainer(((StorageMinecart)h1), null, ContainerType.MINECART)){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent e){
        if(!plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSIONS)) return;
        List<Block> chestBlocks = new ArrayList<>();
        for(Block block : e.blockList()){
            BlockState state = (BlockState) block.getState();
            if(state instanceof Chest){
                if(ChestUtils.isLootinContainer(null, state, ContainerType.CHEST)){
                    chestBlocks.add(block);
                }
            }
            else if(state instanceof Barrel){
                if(ChestUtils.isLootinContainer(null, state, ContainerType.BARREL)){
                    chestBlocks.add(block);
                }
            }
        }
        for (Block block : chestBlocks) {
            e.blockList().remove(block);
        }
    }


    @EventHandler
    public void onMinecartDestroy(VehicleDestroyEvent e){
        if(!(e.getVehicle() instanceof StorageMinecart)) return;
        StorageMinecart chest = (StorageMinecart) e.getVehicle();
        if(ChestUtils.isLootinContainer(e.getVehicle(), null, ContainerType.MINECART)){
            if(plugin.currentMinecartviewers.contains(chest)){
                e.setCancelled(true);
                return;
            }
            if(e.getAttacker() instanceof Player){
                Player player = (Player) e.getAttacker();
                if(player.hasPermission("lootin.breakchest.bypass")){
                    if(player.isSneaking()){
                        chest.getInventory().clear();
    
                        if(!plugin.getConfig().getBoolean(LConstants.DELETE_ITEMS_CONFIG) && ChestUtils.hasPlayerLoot(chest, null, player, ContainerType.MINECART)){
                            List<ItemStack> items = ChestUtils.getContainerItems(chest, null, ContainerType.MINECART, player);
                            if(items != null){
                                items.forEach(i -> {
                                    if(i != null){
                                        player.getWorld().dropItemNaturally(chest.getLocation(), i);
                                    }
                                });
                            }
                        }
                    }
                    else{
                        player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP));
                        e.setCancelled(true);
                    }
                }
                else{
                    e.setCancelled(true);
                    player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP));
                }
            }
            else {
                if(plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSIONS)){
                    e.setCancelled(true);
                }
            }
        }
    }
}
