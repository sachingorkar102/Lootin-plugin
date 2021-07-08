package com.github.sachin.lootin.gui;

import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;

import org.bukkit.block.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BarrelGui extends GuiHolder{

    private Barrel barrel;

    public BarrelGui(Player player,Barrel barrel) {
        super(player, ContainerType.BARREL);
        this.barrel = barrel;
    }

    @Override
    public void open() {
        List<ItemStack> contents = ChestUtils.getContainerItems(null, barrel, type, player);
        if(contents != null){
            inventory.setContents(contents.toArray(new ItemStack[0]));
            player.openInventory(inventory);
            barrel.open();
            plugin.currentChestviewers.add(barrel.getLocation());
        }
    }

    @Override
    public void close() {
        List<ItemStack> contents = Arrays.asList(inventory.getContents());
        if(contents != null){
            ChestUtils.setContainerItems(null, barrel, type, contents, player.getUniqueId().toString());
        }
        barrel.close();
        plugin.currentChestviewers.remove(barrel.getLocation());
    }
    
}
