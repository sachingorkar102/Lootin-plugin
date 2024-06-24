package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BlockTransformer;
import org.bukkit.util.BoundingBox;


public class ChunkLoadListener extends BaseListener{


    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent e){
        Chunk chunk = e.getChunk();
        if(plugin.isBlackListWorld(chunk.getWorld())) return;
        plugin.getScheduler().runTaskLater(plugin,() ->
        {
            if(!chunk.isLoaded()) return;
            boolean isNewChunk = e.isNewChunk();

            for(Entity entity : chunk.getEntities()){
                if(entity.getType()==EntityType.ITEM_FRAME && isNewChunk){
                    if(chunk.getWorld().getEnvironment()==Environment.THE_END && plugin.getConfig().getBoolean(LConstants.PER_PLAYER_ELYTRA_ITEM_FRAME)){
                        ItemFrame frame = (ItemFrame) entity;
                        if(frame.getItem() != null && frame.getItem().getType()==Material.ELYTRA){
                            frame.getPersistentDataContainer().set(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER, 1);
                        }
                    }
                }
                if(entity.getType()==EntityType.MINECART_CHEST){
                    StorageMinecart minecart = (StorageMinecart) entity;
                    if (!ChestUtils.isLootinContainer(minecart, null, ContainerType.MINECART)){
                        if(minecart.getLootTable() == null || plugin.isBlackListedLootable(minecart.getLootTable())) {
                            continue;
                        }
                        ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);

                    }
                }

            }
            for(BlockState b : chunk.getTileEntities()){
                if(b instanceof Lootable){
                    BlockState block = chunk.getWorld().getBlockState(b.getLocation());
                    Lootable lootable = (Lootable) block;
                    if(lootable.getLootTable() == null || plugin.isBlackListedLootable(lootable.getLootTable())) {
                        continue;
                    }
                    boolean isLootin = false;
                    ContainerType container;
//                        plugin.debug(block.getType()+": "+block.getLocation());
                    if (block instanceof Chest) {
                        isLootin = ChestUtils.isLootinContainer(null, block,
                                container = (ChestUtils.isDoubleChest(b) ? ContainerType.DOUBLE_CHEST : ContainerType.CHEST));
                    } else if (block instanceof Barrel) {
                        isLootin = ChestUtils.isLootinContainer(null, block, container = ContainerType.BARREL);
                    }
                    else{continue;}
                    if(!isLootin){
                        ChestUtils.setLootinContainer(null,block,container);
                    }
                }
            }
        },chunk,1);
    }
    
}
