package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.listeners.BaseListener;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;

import otd.api.event.ChestEvent;

public class OTDLootListener extends BaseListener{


    @EventHandler
    public void onEvent(ChestEvent e){
        if(plugin.getBlackListWorlds().contains(e.getLocation().getWorld().getName())) return;
        if(plugin.getConfig().getStringList(LConstants.BLACK_LIST_OTD_STRUCTURES).contains(e.getType().name())) return;
        BlockState state = e.getLocation().getBlock().getState();
        if(state instanceof Barrel){
            ChestUtils.setLootinContainer(null, state, ContainerType.BARREL);
        }
        else if(state instanceof Chest){
            ChestUtils.setLootinContainer(null, state, ContainerType.CHEST);
        }
    }
    
}
