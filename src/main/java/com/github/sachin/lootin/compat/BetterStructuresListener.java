package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.listeners.BaseListener;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.magmaguy.betterstructures.api.ChestFillEvent;
import org.bukkit.block.Chest;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BetterStructuresListener implements Listener {


    @EventHandler
    public void onChestFill(ChestFillEvent e){
        if(e.getContainer() instanceof Chest){
            ChestUtils.setLootinContainer(null,e.getContainer(), ContainerType.CHEST);
        }
    }



}
