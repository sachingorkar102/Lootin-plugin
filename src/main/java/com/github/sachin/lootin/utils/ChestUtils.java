package com.github.sachin.lootin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.sachin.lootin.Lootin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Class for loot container utils
 */
public class ChestUtils{

    public static boolean hasPlayerLoot(@Nullable Entity minecart,@Nullable BlockState block,@NotNull Player player,@NotNull ContainerType type){
        NamespacedKey key = Lootin.getKey(player.getUniqueId().toString());
        if(type == ContainerType.CHEST){
            Chest chest = (Chest) block;
            PersistentDataContainer data = chest.getPersistentDataContainer();
            return data.has(key, PersistentDataType.STRING);
        }
        else if(type == ContainerType.BARREL){
            Barrel barrel = (Barrel) block;
            PersistentDataContainer data = barrel.getPersistentDataContainer();
            return data.has(key, PersistentDataType.STRING);
        }
        else if(type == ContainerType.MINECART){
            StorageMinecart tileCart = (StorageMinecart) minecart;
            PersistentDataContainer data = tileCart.getPersistentDataContainer();
            return data.has(key,PersistentDataType.STRING);
        }
        else if(type == ContainerType.DOUBLE_CHEST){
            DoubleChest doubleChest = getDoubleChest(block);
            PersistentDataContainer d1 = ((Chest)doubleChest.getLeftSide()).getPersistentDataContainer();
            PersistentDataContainer d2 = ((Chest)doubleChest.getRightSide()).getPersistentDataContainer();
            return d1.has(key, PersistentDataType.STRING) && d2.has(key, PersistentDataType.STRING);
        }
        return false;
    }


    /**
     * Determines weather given container has lootin tag or not
     * @param minecart only StorageMinecart if container is a Entity or can be null
     * @param block Blockstate of container or can be null if using minecart
     * @param type ContainerType {@link com.github.sachin.lootin.utils.ContainerType}
     * @return weather given container has lootin tag or not
     */
    public static boolean isLootinContainer(@Nullable Entity minecart,@Nullable BlockState block,@NotNull ContainerType type){
        if(type == ContainerType.MINECART && minecart != null){
            return hasKey(((StorageMinecart)minecart).getPersistentDataContainer());
        }
        if(block != null){
            if(type == ContainerType.CHEST){
                return hasKey(((Chest)block).getPersistentDataContainer());
            }
            if(type == ContainerType.BARREL){
                return hasKey(((Barrel)block).getPersistentDataContainer());
            }
            if(type == ContainerType.DOUBLE_CHEST && isDoubleChest(block)){
                DoubleChest doubleChest = getDoubleChest(block);
                PersistentDataContainer d1 = ((Chest)doubleChest.getLeftSide()).getPersistentDataContainer();
                PersistentDataContainer d2 = ((Chest)doubleChest.getRightSide()).getPersistentDataContainer();
                return hasKey(d1) && hasKey(d2);
            }
        }
        return false;
    }

    /**
     * Sets a lootin tag for container
     * @param minecart only StorageMinecart if container is a Entity or can be null
     * @param block Blockstate of container or can be null if using minecart
     * @param type ContainerType {@link com.github.sachin.lootin.utils.ContainerType}
     */
    public static void setLootinContainer(@Nullable Entity minecart,@Nullable BlockState block,@NotNull ContainerType type){
        if(isLootinContainer(minecart, block, type)) return;
        if(type == ContainerType.CHEST){
            Chest chest = (Chest) block;
            PersistentDataContainer data = chest.getPersistentDataContainer();
            data.set(LConstants.IDENTITY_KEY, PersistentDataType.STRING, "");
            chest.update();
        }
        else if(type == ContainerType.MINECART){
            minecart.getPersistentDataContainer().set(LConstants.IDENTITY_KEY, PersistentDataType.STRING, "");
            

        }
        else if(type == ContainerType.DOUBLE_CHEST && isDoubleChest(block)){
            DoubleChest doubleChest = getDoubleChest(block);
            Chest c1 = ((Chest)doubleChest.getLeftSide());
            Chest c2 = ((Chest)doubleChest.getRightSide());
            setLootinContainer(null, c1, ContainerType.CHEST);
            setLootinContainer(null, c2, ContainerType.CHEST);
        }
        else if(type == ContainerType.BARREL){
            Barrel barrel = (Barrel) block;
            PersistentDataContainer data = barrel.getPersistentDataContainer();
            data.set(LConstants.IDENTITY_KEY,PersistentDataType.STRING,"");
            barrel.update();
        }
    }

    /**
     * Retrives list of items unique to player or the default loot from lootable if there isnt any loot uique to player yet
     * @param minecart only StorageMinecart if container is a Entity or can be null
     * @param block Blockstate of container or can be null if using minecart
     * @param type ContainerType {@link com.github.sachin.lootin.utils.ContainerType}
     * @param player Player object whose unique loot will be retrived
     * @return list of items unique to player
     */
    public static List<ItemStack> getContainerItems(@Nullable Entity minecart,@Nullable BlockState block,@NotNull ContainerType type,@NotNull Player player){
        String uuid = player.getUniqueId().toString();
        PersistentDataContainer data = null;
        Inventory inventory = null;
        LootTable loottable = null;
        long seed = 0;
        if(type == ContainerType.CHEST){
            Chest chest = (Chest) block;
            data = chest.getPersistentDataContainer();
            inventory = chest.getBlockInventory();
            loottable = chest.getLootTable();
            if(loottable == null) {
                seed = chest.getSeed();
                chest.setLootTable(null);
            }
        }
        else if(type == ContainerType.MINECART){
            StorageMinecart tileCart = (StorageMinecart) minecart;
            data = tileCart.getPersistentDataContainer();
            inventory = tileCart.getInventory();
            loottable = tileCart.getLootTable();
            if(loottable == null) {
                seed = tileCart.getSeed();
                tileCart.setLootTable(null);
            }
        }
        else if(type == ContainerType.BARREL){
            Barrel barrel = (Barrel) block;
            data = barrel.getPersistentDataContainer();
            inventory = barrel.getInventory();
            loottable = barrel.getLootTable();
            if(loottable == null) {
                seed = barrel.getSeed();
                barrel.setLootTable(null);
            }
        }
        else if(type == ContainerType.DOUBLE_CHEST && isDoubleChest(block)){
            DoubleChest doubleChest = getDoubleChest(block);
            Chest c1 = ((Chest)doubleChest.getLeftSide());
            Chest c2 = ((Chest)doubleChest.getRightSide());
            List<ItemStack> c1Items = getContainerItems(null, c1, ContainerType.CHEST, player);
            List<ItemStack> c2Items = getContainerItems(null, c2, ContainerType.CHEST, player);
            List<ItemStack> combined = new ArrayList<>();
            for (ItemStack itemStack : c1Items)combined.add(itemStack);
            for (ItemStack itemStack : c2Items)combined.add(itemStack);
            return combined;
        }
        else{
            return null;
        }



        if(data.has(Lootin.getKey(uuid), PersistentDataType.STRING)){
            return ItemSerializer.deserialize(data.get(Lootin.getKey(uuid), PersistentDataType.STRING));
        }
        else if(data.has(LConstants.DATA_KEY, PersistentDataType.STRING)){
            return ItemSerializer.deserialize(data.get(LConstants.DATA_KEY, PersistentDataType.STRING));
        } else {
            List<ItemStack> chestContents = Arrays.asList(inventory.getContents());
            setContainerItems(minecart, block, type, chestContents, LConstants.DATA_KEY_STRING);
            inventory.clear();
            return chestContents;
        }
    }

    /**
     * Stores the list of items into given container
     * @param minecart only StorageMinecart if container is a Entity or can be null
     * @param block Blockstate of container or can be null if using minecart
     * @param type ContainerType {@link com.github.sachin.lootin.utils.ContainerType}
     * @param items list of items to be stored
     * @param key a key, generally use player's uuid as key
     */
    public static void setContainerItems(@Nullable Entity minecart,@Nullable BlockState block,@NotNull ContainerType type,@NotNull List<ItemStack> items,String key){
        PersistentDataContainer data;
        if(type == ContainerType.CHEST){
            Chest chest = (Chest) block;
            data = chest.getPersistentDataContainer();
            data.set(Lootin.getKey(key), PersistentDataType.STRING, ItemSerializer.serialize(items));
            chest.update();
        }
        else if(type == ContainerType.MINECART){
            StorageMinecart tileCart = (StorageMinecart) minecart;
            data = tileCart.getPersistentDataContainer();
            data.set(Lootin.getKey(key), PersistentDataType.STRING, ItemSerializer.serialize(items));
            
        }
        else if(type == ContainerType.BARREL){
            Barrel barrel = (Barrel) block;
            data = barrel.getPersistentDataContainer();
            data.set(Lootin.getKey(key),PersistentDataType.STRING,ItemSerializer.serialize(items));
            barrel.update();
        }
        else if(type == ContainerType.DOUBLE_CHEST && isDoubleChest(block)){
            DoubleChest doubleChest = getDoubleChest(block);
            Chest c1 = ((Chest)doubleChest.getLeftSide());
            Chest c2 = ((Chest)doubleChest.getRightSide());
            setContainerItems(null, c1, ContainerType.CHEST, items.subList(0, 26), key);
            setContainerItems(null, c2, ContainerType.CHEST, items.subList(26, 53), key);
        }
    }

    public static DoubleChest getDoubleChest(BlockState block){
        Chest chest = (Chest) block;
        return ((DoubleChest)chest.getInventory().getHolder());
    }

    public static boolean isDoubleChest(BlockState block){
        return (block instanceof Chest) && (((Chest)block).getInventory().getHolder() instanceof DoubleChest);
     }


    private static boolean hasKey(PersistentDataContainer data){
        return data.has(LConstants.IDENTITY_KEY, PersistentDataType.STRING);
    }

    public static boolean isChest(Material mat){
        return mat==Material.CHEST || mat==Material.TRAPPED_CHEST;
    }
}