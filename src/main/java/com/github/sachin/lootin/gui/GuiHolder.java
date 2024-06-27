package com.github.sachin.lootin.gui;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.ContainerType;

import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;

public class GuiHolder implements InventoryHolder{


    protected Player player;
    protected ContainerType type;
    protected Inventory inventory;
    protected Lootin plugin;
    protected InventoryHolder container;

    public GuiHolder(Player player,ContainerType type){
        this.player = player;
        this.type = type;
        this.plugin = Lootin.getPlugin();
        this.inventory = Bukkit.createInventory(this, type.getSlots(),type.getTitle(player));
    }


    public void handleClickEvents(InventoryClickEvent e){
        if(plugin.getConfig().getBoolean(LConstants.PREVENT_ITEM_FILLING_ENABLED)){
            if(player.hasPermission("lootin.preventfilling.bypass")) return;
            Inventory inv = e.getClickedInventory();
            boolean cancelled = false;
            if(inv == null) return;
            if(!(inv instanceof PlayerInventory)){
                if(e.getAction().toString().startsWith("PLACE_") || e.getAction()==InventoryAction.HOTBAR_SWAP || e.getAction()==InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction()==InventoryAction.SWAP_WITH_CURSOR){
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

    public Player getPlayer() {
        return player;
    }

    public ContainerType getType() {
        return type;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public InventoryHolder getContainer() {
        return container;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void open(){}
    public void close(){}
    
}
