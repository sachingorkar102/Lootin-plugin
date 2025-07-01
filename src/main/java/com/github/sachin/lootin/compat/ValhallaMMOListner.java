package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.listeners.BaseListener;
import com.github.sachin.lootin.listeners.InventoryListeners;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import me.athlaeos.valhallammo.event.ValhallaLootPopulateEvent;
import me.athlaeos.valhallammo.event.ValhallaLootReplacementEvent;
import me.athlaeos.valhallammo.loot.LootTableRegistry;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValhallaMMOListner extends BaseListener {

    public static List<Location> firstTimerChests = new ArrayList<>();

    @EventHandler
    public void onValhallaLootPopulate(ValhallaLootPopulateEvent e){
        Lootable lootable = getLootable(e.getContext());
        System.out.println(e.getContext().getLocation());
        if(lootable !=null){
            Location location = e.getContext().getLocation();
            firstTimerChests.add(location);
            openInventory(lootable,(Player) e.getContext().getLootedEntity(),location);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e){
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = e.getPlayer();
        BlockState state = e.getClickedBlock().getState();;
        if ((e.useInteractedBlock() == PlayerInteractEvent.Result.DENY && !plugin.getConfig().getBoolean(LConstants.BYPASS_GREIF_PLUGINS))) {
            return;
        }
        if(state instanceof Lootable && ChestUtils.getContainerType((Lootable)state) != null){
            Lootable lootable = (Lootable) state;
            ContainerType containerType = ChestUtils.getContainerType(lootable);
            LootTable lootTable = lootable.getLootTable();
            if(ChestUtils.isLootinContainer(lootable,containerType)) return;
            if(lootTable != null){
                if(!plugin.isBlackListedLootable(lootTable,player.getWorld())){
                    ChestUtils.setLootinContainer(lootable,containerType);
                }
            }
            if(ChestUtils.isLootinContainer(lootable,containerType)){
                e.setUseInteractedBlock(PlayerInteractEvent.Result.DENY);
                ChestUtils.openLootinInventory(lootable,player,state.getLocation(),null);
                firstTimerChests.add(state.getLocation());
                System.out.println("a");
            }
        }
    }



    public Lootable getLootable(LootContext context){
        BlockState block = context.getLocation().getBlock().getState();
        if(block instanceof Lootable && ChestUtils.getContainerType((Lootable) block)!=null){
            return (Lootable)block;
        }
        if(context.getLocation().getNearbyEntities(0.1,0.1,0.1).stream().anyMatch(en -> en instanceof StorageMinecart)){
            return (Lootable) context.getLocation().getNearbyEntities(0.1,0.1,0.1).stream().filter(en -> en instanceof StorageMinecart).findFirst().get();
        }
        return null;
    }

    public static void openInventory(Lootable lootable,Player player,Location location){
        Lootin.getPlugin().getScheduler().runTaskLater(Lootin.getPlugin(),() -> {
            InventoryHolder inventoryHolder = (InventoryHolder) lootable;
            ChestUtils.openLootinInventory(lootable,player,location, Arrays.asList(inventoryHolder.getInventory().getContents()));
        },location,1);
    }

    public Location getLocation(Lootable lootable){
        if(lootable instanceof BlockState) return ((BlockState)lootable).getLocation();
        else return  ((Entity)lootable).getLocation();
    }




}
