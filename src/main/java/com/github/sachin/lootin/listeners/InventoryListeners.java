package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.gui.BarrelGui;
import com.github.sachin.lootin.gui.ChestGui;
import com.github.sachin.lootin.gui.DoubleChestGui;
import com.github.sachin.lootin.gui.GuiHolder;
import com.github.sachin.lootin.gui.MinecartGui;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import com.github.sachin.lootin.utils.cooldown.Cooldown;

import org.bukkit.GameMode;
import org.bukkit.Material;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        BlockState state = block.getState();
        Material type = state.getType();
        boolean isLootin = false;
        ContainerType container;
        if(plugin.isRunningWG && !plugin.getWGflag().queryFlag(player,block.getLocation())) return;
        if(plugin.isBlackListWorld(player.getWorld())) return;

        if (ChestUtils.isChest(type)) {
            container = ChestUtils.isDoubleChest(state) ? ContainerType.DOUBLE_CHEST : ContainerType.CHEST;
        } else if (type == Material.BARREL) {
            container = ContainerType.BARREL;
        } else {
            return;
        }
        if (state instanceof Lootable) {
            Lootable lootable = (Lootable) state;
            LootTable lootTable = lootable.getLootTable();
            if (lootTable != null) {
                if (plugin.isBlackListedLootable(lootTable)) {
                    return;
                }
                ChestUtils.setLootinContainer(null, state, container);
            }
        }

        isLootin = ChestUtils.isLootinContainer(null,state,container);

        if (!isLootin || (e.useInteractedBlock() == PlayerInteractEvent.Result.DENY
                && !plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS))) {
//            plugin.debug("Loot Container at "+state.getX()+" "+state.getY()+" "+state.getZ()+" is not a lootin container or player "+player.getName()+" is not allowed to open it");
            return;
        }
        if (player.isSneaking() || player.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
            return;
        }
        e.setUseInteractedBlock(PlayerInteractEvent.Result.DENY);
        Cooldown cooldown = plugin.interactCooldown.get(player.getUniqueId());
        if (!cooldown.isTriggerable()) {
            return;
        }
        cooldown.trigger();
        switch (container) {
            case CHEST:
                if (plugin.currentChestviewers.contains(block.getLocation()))
                    return;
                new ChestGui(player, (Chest) state).open();
                return;
            case DOUBLE_CHEST:
                if (plugin.currentChestviewers.contains(block.getLocation()))
                    return;
                new DoubleChestGui(player, state).open();
                return;
            case BARREL:
                if (plugin.currentChestviewers.contains(block.getLocation()))
                    return;
                new BarrelGui(player, (Barrel) state).open();
                return;
            default:
                return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMinecartInteract(PlayerInteractEntityEvent e) {
        if(plugin.isBlackListWorld(e.getPlayer().getWorld())) return;
        if (!(e.getRightClicked() instanceof StorageMinecart)) {
            return;
        }
        StorageMinecart minecart = (StorageMinecart) e.getRightClicked();
        if(plugin.isRunningWG && !plugin.getWGflag().queryFlag(e.getPlayer(),minecart.getLocation())) return;
        if (!ChestUtils.isLootinContainer(minecart, null, ContainerType.MINECART)){
            if(minecart.getLootTable() == null || plugin.isBlackListedLootable(minecart.getLootTable())) {
                return;
            }
            ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);
            
        }
        if (e.isCancelled() && !plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS)) {
            return;
        }
        e.setCancelled(true);
        Player player = e.getPlayer();
        if (player.isSneaking() || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        Cooldown cooldown = plugin.interactCooldown.get(player.getUniqueId());
        if (!cooldown.isTriggerable()) {
            return;
        }
        cooldown.trigger();
        if (plugin.currentMinecartviewers.contains(minecart))
            return;
        new MinecartGui(player, minecart).open();
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e){
        if(e.getInventory().getHolder() instanceof GuiHolder){
            ((GuiHolder)e.getInventory().getHolder()).handleClickEvents(e);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e){
        if(e.getInventory().getHolder() instanceof GuiHolder){
            ((GuiHolder)e.getInventory().getHolder()).handleDragEvents(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            ((GuiHolder) e.getInventory().getHolder()).close();
        }
    }
}
