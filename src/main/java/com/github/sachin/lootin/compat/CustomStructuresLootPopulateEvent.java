package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.listeners.BaseListener;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import com.ryandw11.structure.api.LootPopulateEvent;

import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;

public class CustomStructuresLootPopulateEvent extends BaseListener{


    @EventHandler
    public void onEvent(LootPopulateEvent e){
        if(plugin.getBlackListWorlds().contains(e.getLocation().getWorld().getName())) return;
        if(plugin.getConfig().getStringList(LConstants.BLACK_LIST_CUSTOM_STRUCTURES).contains(e.getStructure().getName())) return;
        BlockState block = e.getLocation().getBlock().getState();
        if(block instanceof Chest){
            ChestUtils.setLootinContainer(null, block, ContainerType.CHEST);
        }
    }
    
}
