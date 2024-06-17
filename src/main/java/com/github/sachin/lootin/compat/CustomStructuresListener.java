package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.listeners.BaseListener;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import com.ryandw11.structure.CustomStructures;
import com.ryandw11.structure.api.LootPopulateEvent;

import com.ryandw11.structure.loottables.LootTable;
import com.ryandw11.structure.loottables.LootTableType;
import com.ryandw11.structure.schematic.LootTableReplacer;
import com.ryandw11.structure.structure.Structure;
import com.ryandw11.structure.utils.RandomCollection;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class CustomStructuresListener extends BaseListener{


    @EventHandler
    public void onEvent(LootPopulateEvent e){
        if(plugin.getBlackListWorlds().contains(e.getLocation().getWorld().getName())) return;
        if(plugin.getConfig().getStringList(LConstants.BLACK_LIST_CUSTOM_STRUCTURES).contains(e.getStructure().getName())) return;
        BlockState block = e.getLocation().getBlock().getState();
        if(block instanceof Chest || block instanceof Barrel){
            Container container = (Container) block;
            ChestUtils.setLootinContainer(null, block, block instanceof Chest ? ContainerType.CHEST : ContainerType.BARREL);
            container.getPersistentDataContainer().set(LConstants.CUSTOM_STRUC_KEY, PersistentDataType.STRING,e.getStructure().getName());
            container.update();
        }
    }

    public static void reFillContainer(Container container){
        String strucName = container.getPersistentDataContainer().get(LConstants.CUSTOM_STRUC_KEY,PersistentDataType.STRING);
        if(strucName == null) return;
        Structure structure = CustomStructures.getInstance().getStructureHandler().getStructure(strucName);
        if(structure==null) return;
        RandomCollection<LootTable> loottables = structure.getLootTables(container instanceof Chest ? LootTableType.CHEST : LootTableType.BARREL);
        if(loottables != null && !loottables.isEmpty()){
            container.getSnapshotInventory().clear();
            LootTableReplacer.replaceChestContent(loottables.next(),new Random(),container.getSnapshotInventory());
        }
    }


    
}
