package com.github.sachin.lootin.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.StructureGrowEvent;
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
        ContainerType containerType = getContainerType(state);
        if (containerType == null) return;

        Lootable lootable = (Lootable) state;
        if (!ChestUtils.isLootinContainer(null, state, containerType) && lootable.getLootTable() == null) return;



        if (plugin.currentChestviewers.contains(state.getLocation())) {
            player.sendMessage(plugin.getMessage(LConstants.CHEST_EDITED, player));
            e.setCancelled(true);
            return;
        }

        if (!player.hasPermission("lootin.breakchest.bypass")) {
            e.setCancelled(true);
            player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP, player));
            return;
        }

        if (!player.isSneaking()) {
            player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP, player));
            e.setCancelled(true);
            return;
        }

        ((InventoryHolder) state).getInventory().clear();
        state.update(true);

        if (!plugin.getConfig().getBoolean(LConstants.DELETE_ITEMS_CONFIG)) {
            dropContainerItems(null, state, containerType, player, block.getLocation());
        }
    }

    @EventHandler
    public void onMinecartDestroy(VehicleDestroyEvent e) {
        if (!(e.getVehicle() instanceof StorageMinecart)) return;
        StorageMinecart cart = (StorageMinecart) e.getVehicle();
        if (plugin.isBlackListWorld(cart.getWorld())) return;

        if (!ChestUtils.isLootinContainer(cart, null, ContainerType.MINECART) && cart.getLootTable() == null) return;

        if (plugin.currentMinecartviewers.contains(cart)) {
            e.setCancelled(true);
            return;
        }

        if (e.getAttacker() instanceof Player) {
            Player player = (Player) e.getAttacker();
            if (!player.hasPermission("lootin.breakchest.bypass")) {
                e.setCancelled(true);
                player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHOUTP, player));
                return;
            }

            if (!player.isSneaking()) {
                e.setCancelled(true);
                player.sendMessage(plugin.getMessage(LConstants.BLOCK_BREAK_WITHP, player));
                return;
            }

            cart.getInventory().clear();
            if (!plugin.getConfig().getBoolean(LConstants.DELETE_ITEMS_CONFIG)) {
                dropContainerItems(cart, null, ContainerType.MINECART, player, cart.getLocation());
            }
        } else if (plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSIONS)) {
            e.setCancelled(true);
        }
    }

    private ContainerType getContainerType(BlockState state) {
        if (state instanceof Chest) return ContainerType.CHEST;
        if (state instanceof Barrel) return ContainerType.BARREL;
        return null;
    }

    private void dropContainerItems(StorageMinecart cart, BlockState state, ContainerType containerType, Player player, Location dropLocation) {
        List<ItemStack> items = ChestUtils.getContainerItems(cart, state, containerType, player);
        if (items == null) return;

        Location drop = dropLocation.clone().add(0.5, 0.5, 0.5);
        for (ItemStack item : items) {
            if (item != null) {
                player.getWorld().dropItemNaturally(drop, item);
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

//    For Creepers and Ignited TNT
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSIONS) || plugin.isBlackListWorld(e.getEntity().getWorld())) return;
        removeLootContainersFromList(e.blockList());
    }

//    For Respawn_Anchors and Beds
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (!plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSIONS) || plugin.isBlackListWorld(e.getBlock().getWorld())) return;
        BlockState explodedBlock = e.getExplodedBlockState();
        if (explodedBlock != null && (explodedBlock.getType() == Material.RESPAWN_ANCHOR || explodedBlock.getType().toString().endsWith("BED"))) {
            removeLootContainersFromList(e.blockList());
        }
    }

    @EventHandler
    public void onMushroomGrowEvent(StructureGrowEvent e) {
        if ((e.getSpecies() == TreeType.BROWN_MUSHROOM || e.getSpecies() == TreeType.RED_MUSHROOM) && e.isFromBonemeal()) {
            boolean hasLootContainer = e.getBlocks().stream().anyMatch(blockState -> isLootContainer(blockState.getWorld().getBlockAt(blockState.getLocation())));
            if (hasLootContainer) e.setCancelled(true);
        }
    }

    private void removeLootContainersFromList(List<Block> blocks) {
        blocks.removeIf(this::isLootContainer);
    }

    private boolean isLootContainer(Block block) {
        BlockState state = block.getState();
        if (state instanceof Chest || state instanceof Barrel) {
            Lootable lootable = (Lootable) state;
            return lootable.getLootTable() != null || ChestUtils.isLootinContainer(
                    null,
                    state,
                    state instanceof Chest ? ContainerType.CHEST : ContainerType.BARREL
            );
        }
        return false;
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
