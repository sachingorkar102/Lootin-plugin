package com.github.sachin.lootin.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestEvents extends BaseListener{
    

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChestBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if(plugin.isBlackListWorld(player.getWorld())) return;
        if(ChestUtils.isChest(block.getType())){
            Chest chest = (Chest) block.getState();
            if(ChestUtils.isLootinContainer(null, chest, ContainerType.CHEST)){
                if(plugin.currentChestviewers.contains(chest.getLocation())){
                    player.sendMessage(plugin.getMessage(LConstants.CHEST_EDITED,player));
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
                        player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP,player));
                        e.setCancelled(true);
                    }
                }
                else{
                    e.setCancelled(true);
                    player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP,player));
                }
            }
        }
        else if(block.getType() == Material.BARREL){
            Barrel barrel = (Barrel) block.getState();
            if(ChestUtils.isLootinContainer(null, barrel, ContainerType.BARREL)){
                if(plugin.currentChestviewers.contains(barrel.getLocation())){
                    player.sendMessage(plugin.getMessage(LConstants.CHEST_EDITED,player));
                    e.setCancelled(true);
                    return;
                }
                if(player.hasPermission("lootin.breakchest.bypass")){
                    if(player.isSneaking()){
                        barrel.getInventory().clear();

                        if(!plugin.getConfig().getBoolean(LConstants.DELETE_ITEMS_CONFIG) && ChestUtils.hasPlayerLoot(null, barrel, player, ContainerType.BARREL)){
                            List<ItemStack> items = ChestUtils.getContainerItems(null, barrel, ContainerType.BARREL, player);
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
                        player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP,player));
                        e.setCancelled(true);
                    }
                }
                else{
                    e.setCancelled(true);
                    player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP,player));
                }
            }
        }
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e){
        List<InventoryHolder> holders = Arrays.asList(e.getSource().getHolder(),e.getDestination().getHolder());
//        if(plugin.isBlackListWorld(e.ge)) return;
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
            else if(h1 instanceof Chest){
                if(ChestUtils.isLootinContainer(null, ((Chest)h1), ContainerType.CHEST)){
                    e.setCancelled(true);
                }
            }
            else if(h1 instanceof StorageMinecart){
                if(ChestUtils.isLootinContainer(((StorageMinecart)h1), null, ContainerType.MINECART)){
                    e.setCancelled(true);
                }
            }
            else if(h1 instanceof Barrel){
                if(ChestUtils.isLootinContainer(null, ((Barrel)h1), ContainerType.BARREL)){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent e){
        if(!plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSIONS) || plugin.isBlackListWorld(e.getEntity().getWorld())) return;
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
        if(!(e.getVehicle() instanceof StorageMinecart) || plugin.isBlackListWorld(e.getVehicle().getWorld())) return;
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
                        player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP,player));
                        e.setCancelled(true);
                    }
                }
                else{
                    e.setCancelled(true);
                    player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP,player));
                }
            }
            else {
                if(plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSIONS)){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChestPlace(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getItem() == null) return;
        if(!ChestUtils.isChest(e.getMaterial())) return;
        Block b = e.getClickedBlock().getRelative(e.getBlockFace());
        Player player = e.getPlayer();
        if(plugin.isBlackListWorld(player.getWorld())) return;
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!ChestUtils.isChest(b.getType())) {
                    return;
                }
                for(BlockFace face : Arrays.asList(BlockFace.EAST,BlockFace.WEST,BlockFace.SOUTH,BlockFace.NORTH)){
                    Block block = b.getRelative(face);
                    if(block.getState() instanceof Chest){
                        Chest chest = (Chest) block.getState();
                        if(ChestUtils.isLootinContainer(null, chest, ContainerType.CHEST)){
                            b.setType(Material.AIR);
                            player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(e.getMaterial()));
                            player.sendMessage(plugin.getMessage(LConstants.CANT_PLACE_DCHEST, player));
                            break;
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 1);
    }
}
