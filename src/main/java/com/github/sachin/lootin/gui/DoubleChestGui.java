package com.github.sachin.lootin.gui;

import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DoubleChestGui extends GuiHolder{

    private BlockState block;
    private DoubleChest doubleChest;

    public DoubleChestGui(Player player, BlockState block) {
        super(player, ContainerType.DOUBLE_CHEST);
        this.block = block;
        this.doubleChest = ChestUtils.getDoubleChest(block);
        this.container = this.doubleChest;
    }

    @Override
    public void open() {
        List<ItemStack> items = ChestUtils.getContainerItems(null, block, type, player);
        if(items != null){
            inventory.setContents(items.toArray(new ItemStack[0]));
            player.openInventory(inventory);
            ((Chest)block).open();
            plugin.currentChestviewers.add(((Chest)doubleChest.getLeftSide()).getLocation());
            plugin.currentChestviewers.add(((Chest)doubleChest.getRightSide()).getLocation());
        }
    }

    @Override
    public void close() {
        List<ItemStack> contents = Arrays.asList(inventory.getContents());
        if(contents != null){
            ChestUtils.setContainerItems(null, block, type, contents, player.getUniqueId().toString());
        }
        ((Chest)block).close();
        plugin.currentChestviewers.remove(((Chest)doubleChest.getLeftSide()).getLocation());
        plugin.currentChestviewers.remove(((Chest)doubleChest.getRightSide()).getLocation());
    }
    
}
