package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;

import org.bukkit.Chunk;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadListener extends BaseListener{
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        // if(!e.isNewChunk()) return;
        Chunk chunk = e.getChunk();
        if(!chunk.isLoaded()) return;
        if(plugin.getBlackListWorlds().contains(chunk.getWorld().getName())) return;
        for(BlockState tile : chunk.getTileEntities()){
            if(tile instanceof Chest){
                if(((Chest)tile).getLootTable() != null){
                    if(!plugin.getBlackListStructures().contains(((Chest)tile).getLootTable().getKey())){
                        ChestUtils.setLootinContainer(null, tile, ContainerType.CHEST);
                        
                    }
                }
            }
            if(tile instanceof Barrel){
                if(((Barrel)tile).getLootTable() != null){
                    if(!plugin.getBlackListStructures().contains(((Barrel)tile).getLootTable().getKey())){
                        ChestUtils.setLootinContainer(null, tile, ContainerType.BARREL);
                        
                    }
                }
            }
        }
        for(Entity entity : chunk.getEntities()){
            if(entity instanceof StorageMinecart){
                StorageMinecart minecart = (StorageMinecart) entity;
                if(minecart.getLootTable() != null){
                    if(!plugin.getBlackListStructures().contains(minecart.getLootTable().getKey())){
                        ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);
                    }
                }
            }
        }
    }
    
}
