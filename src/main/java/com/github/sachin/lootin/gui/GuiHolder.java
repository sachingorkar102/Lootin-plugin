package com.github.sachin.lootin.gui;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.ContainerType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiHolder implements InventoryHolder{


    protected Player player;
    protected ContainerType type;
    protected Inventory inventory;
    protected Lootin plugin;

    public GuiHolder(Player player,ContainerType type){
        this.player = player;
        this.type = type;
        this.plugin = Lootin.getPlugin();
        this.inventory = Bukkit.createInventory(this, type.getSlots(),type.getTitle(player));
    }


    public void handleClickEvents(InventoryClickEvent e){

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

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void open(){}
    public void close(){}
    
}
