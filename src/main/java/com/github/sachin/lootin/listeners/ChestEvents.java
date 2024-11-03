package com.github.sachin.lootin.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
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
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ChestEvents extends BaseListener{

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChestBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if (plugin.isBlackListWorld(player.getWorld())) return;

        BlockState state = block.getState();
        ContainerType containerType = null;

        if (state instanceof Chest) {
            containerType = ContainerType.CHEST;
        } else if (state instanceof Barrel) {
            containerType = ContainerType.BARREL;
        }

        if (containerType != null) {
            Lootable lootable = (Lootable) state;
            if(ChestUtils.isLootinContainer(null,state,containerType) || lootable.getLootTable() != null){
                if (plugin.currentChestviewers.contains(state.getLocation())) {
                    player.sendMessage(plugin.getMessage(LConstants.CHEST_EDITED, player));
                    e.setCancelled(true);
                    return;
                }

                if (player.hasPermission("lootin.breakchest.bypass")) {
                    if (player.isSneaking()) {
                        ((InventoryHolder) state).getInventory().clear();

                        if (!plugin.getConfig().getBoolean(LConstants.DELETE_ITEMS_CONFIG) &&
                                ChestUtils.hasPlayerLoot(null, state, player, containerType)) {

                            List<ItemStack> items = ChestUtils.getContainerItems(null, state, containerType, player);
                            if (items != null) {
                                for (ItemStack item : items) {
                                    if (item != null) {
                                        player.getWorld().dropItemNaturally(block.getLocation(), item);
                                    }
                                }
                            }
                        }
                    } else {
                        player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP, player));
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                    player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP, player));
                }

            }

        }
    }


//    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e){
//        if(plugin.isBlackListWorld(e.ge)) return;
        if(e.getDestination().getHolder() instanceof HopperMinecart || e.getSource().getHolder() instanceof HopperMinecart){
            List<InventoryHolder> holders = Arrays.asList(e.getSource().getHolder(),e.getDestination().getHolder());
            for (InventoryHolder h : holders) {
                if(h == null) continue;
                if(h instanceof HopperMinecart) continue;
                if(h instanceof DoubleChest){
                    DoubleChest doubleChest = (DoubleChest) h;
                    PersistentDataContainer d1 = ((Chest)doubleChest.getLeftSide()).getPersistentDataContainer();
                    PersistentDataContainer d2 = ((Chest)doubleChest.getRightSide()).getPersistentDataContainer();
                    if(d1.has(LConstants.IDENTITY_KEY, PersistentDataType.STRING) || d2.has(LConstants.IDENTITY_KEY, PersistentDataType.STRING)){
                        e.setCancelled(true);
                    }
                }
                else if(h instanceof Chest){
                    if(ChestUtils.isLootinContainer(null, ((Chest)h), ContainerType.CHEST)){
                        e.setCancelled(true);
                    }
                }
                else if(h instanceof StorageMinecart){
                    if(ChestUtils.isLootinContainer(((StorageMinecart)h), null, ContainerType.MINECART)){
                        e.setCancelled(true);
                    }
                }
                else if(h instanceof Barrel){
                    if(ChestUtils.isLootinContainer(null, ((Barrel)h), ContainerType.BARREL)){
                        e.setCancelled(true);
                    }
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
                Chest chest = (Chest) state;
                if(ChestUtils.isLootinContainer(null, state, ContainerType.CHEST) || chest.getLootTable() != null){
                    chestBlocks.add(block);
                }
            }
            else if(state instanceof Barrel){
                Barrel barrel = (Barrel) state;
                if(ChestUtils.isLootinContainer(null, state, ContainerType.BARREL) || barrel.getLootTable() != null){
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
        if(ChestUtils.isLootinContainer(e.getVehicle(), null, ContainerType.MINECART) || chest.getLootTable() != null){
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


        plugin.getScheduler().runTaskLater(plugin,() ->
        {

            if(!ChestUtils.isChest(b.getType())) {
                return;
            }
            for(BlockFace face : Arrays.asList(BlockFace.EAST,BlockFace.WEST,BlockFace.SOUTH,BlockFace.NORTH,BlockFace.UP,BlockFace.DOWN)){
                Block block = b.getRelative(face);

                if(block.getState() instanceof Chest && !(face == BlockFace.UP || face == BlockFace.DOWN)){
                    Chest chest = (Chest) block.getState();
                    if(ChestUtils.isLootinContainer(null, chest, ContainerType.CHEST)){
                        b.setType(Material.AIR);
                        player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(e.getMaterial()));
                        player.sendMessage(plugin.getMessage(LConstants.CANT_PLACE_DCHEST, player));
                        break;
                    }
                }
            }
        },b.getLocation(),1);
    }
}
