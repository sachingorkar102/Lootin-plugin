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
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;


public class ChunkLoadListener extends BaseListener{
    

    @EventHandler
    public void onRemove(HangingBreakEvent e){
        if(e.getEntity().getPersistentDataContainer().has(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onElytraRemove(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(e.getEntity().getType()==EntityType.ITEM_FRAME && plugin.getConfig().getBoolean(LConstants.PER_PLAYER_ELYTRA_ITEM_FRAME)){
            ItemFrame framea = (ItemFrame) e.getEntity();
            if(!framea.getPersistentDataContainer().has(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER)) return;
            e.setCancelled(true);
            
            if(e.getDamager() instanceof Player){
                Player player = (Player) e.getDamager();
                String uuid = player.getUniqueId().toString();
                NamespacedKey playerKey = Lootin.getKey(uuid);
                if(framea.getPersistentDataContainer().has(playerKey, PersistentDataType.INTEGER)){
                    if(player.hasPermission("lootin.breakelytraitemframe.bypass")){
                        if(player.getInventory().getItemInMainHand().getType()==Material.STICK){
                            framea.remove();
                            player.sendMessage(plugin.getMessage(LConstants.ELYTRA_IF_REMOVED, player));
                        }
                        else{
                            player.sendMessage(plugin.getMessage(LConstants.ELYTRA_IF_BREAK_WITHPERM, player));
                        }
                    }
                    else{
                        player.sendMessage(plugin.getMessage(LConstants.ELYTRA_IF_BREAK_WITHOUTPERM, player));
                    }
                    return;
                }
                
                framea.getPersistentDataContainer().set(playerKey,PersistentDataType.INTEGER,1);
                Location loc = framea.getLocation().getBlock().getLocation();
                BlockFace face = framea.getFacing();
                framea.getWorld().dropItemNaturally(new Location(framea.getWorld(), loc.getX()+(face.getModX()*0.15F), loc.getY()+0.15F, loc.getZ()+(face.getModZ()*0.15F)), new ItemStack(Material.ELYTRA));
                if(plugin.isRunningProtocolLib){
                    framea.setItem(new ItemStack(Material.ELYTRA));
                }
                
            }
        }
    }


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        Chunk chunk = e.getChunk();
        if(e.isNewChunk() && e.getChunk().getWorld().getEnvironment()==Environment.THE_END && plugin.getConfig().getBoolean(LConstants.PER_PLAYER_ELYTRA_ITEM_FRAME)){
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!chunk.isLoaded()) return;
                    for(Entity entity : chunk.getEntities()){
                        if(entity.getType()==EntityType.ITEM_FRAME){
                            ItemFrame frame = (ItemFrame) entity;
                            if(frame.getItem() != null && frame.getItem().getType()==Material.ELYTRA){
                                frame.getPersistentDataContainer().set(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER, 1);
                                
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, 7);
        }
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
