package com.github.sachin.lootin.gui;

import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;

import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;


public class MinecartGui extends GuiHolder{

    private StorageMinecart minecart;

    public MinecartGui(Player player,StorageMinecart minecart) {
        super(player, ContainerType.MINECART);
        this.minecart = minecart;
        this.container = minecart;
    }

    @Override
    public void open() {
        List<ItemStack> contents = ChestUtils.getContainerItems(minecart, null, type, player);
        if(contents != null){
            inventory.setContents(contents.toArray(new ItemStack[0]));
            player.openInventory(inventory);
            plugin.currentMinecartviewers.add(minecart);
        }
        
    }

    @Override
    public void close() {
        List<ItemStack> contents = Arrays.asList(inventory.getContents());
        if(contents != null){
            ChestUtils.setContainerItems(minecart, null, type, contents, player.getUniqueId().toString());
            plugin.currentMinecartviewers.remove(minecart);
        }
    }

    
}
