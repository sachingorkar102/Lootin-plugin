package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Lidded;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class LootinGui implements InventoryHolder {

    protected Player player;
    protected ContainerType type;
    protected Inventory inventory;
    protected Lootin plugin;
    protected InventoryHolder container;
    protected Lootable lootable;
    protected boolean isBlock;
    protected boolean isDoubleChest;

    protected List<ItemStack> overrideItems;

    public LootinGui(Player player, ContainerType type, Lootable lootable, @Nullable List<ItemStack> overrideItems){
        this.player = player;
        this.type = type;
        this.plugin = Lootin.getPlugin();
        this.overrideItems = overrideItems;
        this.lootable = lootable;
        this.isBlock = lootable instanceof BlockState;
        this.isDoubleChest = isBlock && ChestUtils.isDoubleChest((BlockState) lootable);
        this.inventory = Bukkit.createInventory(this, type.getSlots(),type.getTitle(player));
    }


    public void open(){
        List<ItemStack> contents = ChestUtils.getContainerItems(!isBlock ? ((Entity)lootable) : null, isBlock ? ((BlockState)lootable) : null, type, player);
        if(contents != null && overrideItems == null) inventory.setContents(contents.toArray(new ItemStack[0]));
        else if(overrideItems != null) inventory.setContents(overrideItems.toArray(new ItemStack[0]));
        else return;
        player.openInventory(inventory);
        DoubleChest doubleChest = isDoubleChest ? ChestUtils.getDoubleChest((BlockState)lootable) : null;
        if(isBlock){
            ((Lidded)lootable).open();
        }
        if(isDoubleChest){
            Location l1 = ((Chest)doubleChest.getLeftSide()).getLocation();
            Location l2 = ((Chest)doubleChest.getRightSide()).getLocation();
            if(plugin.isRunningPaper){
                player.getWorld().sendGameEvent(player,GameEvent.CONTAINER_OPEN,l1.toVector());
            }
//            plugin.getPrilib().getNmsHandler().triggerGameEvent(player, GameEvent.CONTAINER_OPEN,l1);
            if(((Chest)lootable).getLocation().equals(l2)){
                player.playSound(l2, Sound.BLOCK_CHEST_OPEN,0.5F,1F);
            }
            plugin.currentChestviewers.add(l1);
            plugin.currentChestviewers.add(l2);

        }else{
            if(plugin.isRunningPaper){
                player.getWorld().sendGameEvent(player,GameEvent.CONTAINER_OPEN,player.getLocation().toVector());
            }
//            plugin.getPrilib().getNmsHandler().triggerGameEvent(player, GameEvent.CONTAINER_OPEN,getLocation());

            if(isBlock){
                plugin.currentChestviewers.add(getLocation());
            }else{
                plugin.currentMinecartviewers.add((StorageMinecart)lootable);
            }
        }
    }


    public void close(){
        List<ItemStack> contents = Arrays.asList(inventory.getContents());
        if(contents != null){
            ChestUtils.setContainerItems(!isBlock ? ((Entity)lootable) : null, isBlock ? ((BlockState)lootable) : null, type, contents, player.getUniqueId().toString());
        }
        if(isBlock){
            ((Lidded)lootable).close();
        }
        if(isDoubleChest){
            DoubleChest doubleChest = ChestUtils.getDoubleChest((BlockState)lootable);
            Location l1 = ((Chest)doubleChest.getLeftSide()).getLocation();
            Location l2 = ((Chest)doubleChest.getRightSide()).getLocation();
            if(plugin.isRunningPaper){
                player.getWorld().sendGameEvent(player,GameEvent.CONTAINER_CLOSE,l1.toVector());
            }
//            plugin.getPrilib().getNmsHandler().triggerGameEvent(player, GameEvent.CONTAINER_CLOSE,l1);
            if(((Chest)lootable).getLocation().equals(l2)){
                player.playSound(l2, Sound.BLOCK_CHEST_CLOSE,0.5F,1F);
            }
            plugin.currentChestviewers.remove(l1);
            plugin.currentChestviewers.remove(l2);

        }else{
            if(plugin.isRunningPaper){
                player.getWorld().sendGameEvent(player,GameEvent.CONTAINER_CLOSE,getLocation().toVector());
            }
//            plugin.getPrilib().getNmsHandler().triggerGameEvent(player,GameEvent.CONTAINER_CLOSE,getLocation());
            if(isBlock){
                plugin.currentChestviewers.remove(getLocation());
            }
            else{
                plugin.currentMinecartviewers.remove((StorageMinecart)lootable);
            }
        }
    }

    public void handleClickEvents(InventoryClickEvent e){
        if(plugin.getConfig().getBoolean(LConstants.PREVENT_ITEM_FILLING_ENABLED)){
            if(player.hasPermission("lootin.preventfilling.bypass")) return;
            Inventory inv = e.getClickedInventory();
            boolean cancelled = false;
            if(inv == null) return;
            if(!(inv instanceof PlayerInventory)){
                if(e.getAction().toString().startsWith("PLACE_") || e.getAction()== InventoryAction.HOTBAR_SWAP || e.getAction()==InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction()==InventoryAction.SWAP_WITH_CURSOR){
                    cancelled = true;

                }
            }
            if(inv instanceof PlayerInventory){
                if(e.getAction()==InventoryAction.MOVE_TO_OTHER_INVENTORY){
                    cancelled = true;
                }
                if(e.isShiftClick()) cancelled = true;
            }

            if(cancelled){
                e.setCancelled(true);
                if(plugin.getConfig().getBoolean(LConstants.PREVENT_ITEM_FILLING_MSG)){
                    player.sendMessage(plugin.getMessage(LConstants.CANT_PLACE_ITEMS,player));
                }
            }
        }

    }

    public void handleDragEvents(InventoryDragEvent e){
        if(plugin.getConfig().getBoolean(LConstants.PREVENT_ITEM_FILLING_ENABLED)){
            if(player.hasPermission("lootin.preventfilling.bypass")) return;
            for(int i : e.getRawSlots()){
                if(e.getInventory().getSize()>i){
                    e.setCancelled(true);
                    if(plugin.getConfig().getBoolean(LConstants.PREVENT_ITEM_FILLING_MSG)){
                        player.sendMessage(plugin.getMessage(LConstants.CANT_PLACE_ITEMS,player));
                    }
                    break;
                }
            }
        }
    }

    private Location getLocation(){
        if(isBlock) return ((BlockState)lootable).getLocation();
        else return ((Entity)lootable).getLocation();
    }


    public List<ItemStack> getOverrideItems() {
        return overrideItems;
    }

    public Player getPlayer() {
        return player;
    }

    public ContainerType getType() {
        return type;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public InventoryHolder getContainer() {
        return container;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
