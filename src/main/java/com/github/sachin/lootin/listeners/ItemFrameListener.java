package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ItemFrameListener extends BaseListener{


    @EventHandler
    public void onRemove(HangingBreakEvent e){
        if(e.getEntity().getPersistentDataContainer().has(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onElytraRemove(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(e.getEntity().getType()== EntityType.ITEM_FRAME && plugin.getConfig().getBoolean(LConstants.PER_PLAYER_ELYTRA_ITEM_FRAME)){
            ItemFrame framea = (ItemFrame) e.getEntity();
            if(!framea.getPersistentDataContainer().has(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER)) return;
            e.setCancelled(true);

            if(e.getDamager() instanceof Player){
                Player player = (Player) e.getDamager();
                String uuid = player.getUniqueId().toString();
                NamespacedKey playerKey = Lootin.getKey(uuid);
                if(framea.getPersistentDataContainer().has(playerKey, PersistentDataType.INTEGER)){
                    if(player.hasPermission("lootin.breakelytraitemframe.bypass")){
                        if(player.getInventory().getItemInMainHand().getType()== Material.STICK){
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
}
