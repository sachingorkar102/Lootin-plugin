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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
        if (ChestUtils.isChest(type)) {
            isLootin = ChestUtils.isLootinContainer(null, state,
                    container = (ChestUtils.isDoubleChest(state) ? ContainerType.DOUBLE_CHEST : ContainerType.CHEST));
        } else if (type == Material.BARREL) {
            isLootin = ChestUtils.isLootinContainer(null, state, container = ContainerType.BARREL);
        } else {
            return;
        }
        if (!isLootin || (e.useInteractedBlock() == PlayerInteractEvent.Result.DENY
                && !plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS))) {
            return;
        }
        if (player.isSneaking()) {
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
                new DoubleChestGui(player, (Chest) ((DoubleChest) ((Chest) state).getInventory().getHolder()).getLeftSide()).open();
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
        if (!(e.getRightClicked() instanceof StorageMinecart)
                || !ChestUtils.isLootinContainer(e.getRightClicked(), null, ContainerType.MINECART)) {
            return;
        }
        if (e.isCancelled() && !plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS)) {
            return;
        }
        e.setCancelled(true);
        Player player = e.getPlayer();
        Cooldown cooldown = plugin.interactCooldown.get(player.getUniqueId());
        if (!cooldown.isTriggerable()) {
            return;
        }
        cooldown.trigger();
        StorageMinecart minecart = (StorageMinecart) e.getRightClicked();
        if (plugin.currentMinecartviewers.contains(minecart))
            return;
        new MinecartGui(player, minecart).open();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            ((GuiHolder) e.getInventory().getHolder()).close();
        }
    }
}
