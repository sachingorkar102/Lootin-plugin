package com.github.sachin.lootin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.compat.BetterStructuresListener;

import com.github.sachin.lootin.compat.CustomStructuresListener;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Class for loot container utils
 */
public class ChestUtils{

    private static final Lootin plugin = Lootin.getPlugin();

    public static boolean hasPlayerLoot(@Nullable Entity minecart,@Nullable BlockState block,@NotNull Player player,@NotNull ContainerType type){
        NamespacedKey key = Lootin.getKey(player.getUniqueId().toString());
        if(type == ContainerType.CHEST){
            Chest chest = (Chest) block;
            PersistentDataContainer data = chest.getPersistentDataContainer();
            return data.has(key,PersistentDataType.STRING) || data.has(key, DataType.ITEM_STACK_ARRAY);
        }
        else if(type == ContainerType.BARREL){
            Barrel barrel = (Barrel) block;
            PersistentDataContainer data = barrel.getPersistentDataContainer();
            return data.has(key,PersistentDataType.STRING) || data.has(key, DataType.ITEM_STACK_ARRAY);
        }
        else if(type == ContainerType.MINECART){
            StorageMinecart tileCart = (StorageMinecart) minecart;
            PersistentDataContainer data = tileCart.getPersistentDataContainer();
            return data.has(key,PersistentDataType.STRING) || data.has(key, DataType.ITEM_STACK_ARRAY);
        }
        else if(type == ContainerType.DOUBLE_CHEST){
            DoubleChest doubleChest = getDoubleChest(block);
            PersistentDataContainer d1 = ((Chest)doubleChest.getLeftSide()).getPersistentDataContainer();
            PersistentDataContainer d2 = ((Chest)doubleChest.getRightSide()).getPersistentDataContainer();
            return (d1.has(key,PersistentDataType.STRING) || d1.has(key, DataType.ITEM_STACK_ARRAY)) && (d2.has(key,PersistentDataType.STRING) || d2.has(key, DataType.ITEM_STACK_ARRAY));
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

    public static void fillLoot(Player player,PersistentDataContainer data,Lootable container,Inventory inventory){
        String lootTableKey = null;
        NamespacedKey playerLootKey = Lootin.getKey(player.getUniqueId().toString());
        if(data.has(playerLootKey,PersistentDataType.STRING) || data.has(playerLootKey,DataType.ITEM_STACK_ARRAY)) return;
        if(plugin.isRunningBetterStructures && plugin.getConfig().getBoolean(LConstants.RESET_SEED) && data.has(LConstants.BETTER_STRUC_KEY,PersistentDataType.STRING)){

            Chest chest = (Chest) container;
            BetterStructuresListener.refillChest(chest);
            return;
        }
        if(plugin.isRunningCustomStructures && plugin.getConfig().getBoolean(LConstants.RESET_SEED) && data.has(LConstants.CUSTOM_STRUC_KEY,PersistentDataType.STRING)){
            CustomStructuresListener.reFillContainer((Container) container);
            return;
        }
        if(container.getLootTable() != null){
            lootTableKey = container.getLootTable().getKey().getKey();
            data.set(LConstants.LOOTTABLE_KEY,PersistentDataType.STRING,lootTableKey);
            container.setLootTable(null);
            if(container instanceof BlockState){
                ((BlockState)container).update();
            }
        }
        else if(data.has(LConstants.LOOTTABLE_KEY, PersistentDataType.STRING)){
            lootTableKey = data.get(LConstants.LOOTTABLE_KEY,PersistentDataType.STRING);
        }
        if(lootTableKey != null){
            inventory.clear();
            plugin.getPrilib().getNmsHandler().fill(player,container,lootTableKey,plugin.getConfig().getBoolean(LConstants.RESET_SEED));
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
        String loottable = null;
        if(type == ContainerType.CHEST){
            Chest chest = (Chest) block;
            data = chest.getPersistentDataContainer();
            inventory = chest.getBlockInventory();
            fillLoot(player,data,chest,inventory);
        }
        else if(type == ContainerType.MINECART){
            StorageMinecart tileCart = (StorageMinecart) minecart;
            data = tileCart.getPersistentDataContainer();
            inventory = tileCart.getInventory();
            fillLoot(player,data,tileCart,inventory);
        }
        else if(type == ContainerType.BARREL){
            Barrel barrel = (Barrel) block;
            data = barrel.getPersistentDataContainer();
            inventory = barrel.getInventory();
            fillLoot(player,data,barrel,inventory);
        }
        else if(type == ContainerType.DOUBLE_CHEST && isDoubleChest(block)){
            DoubleChest doubleChest = getDoubleChest(block);
            Chest chestLeft = ((Chest) doubleChest.getLeftSide());
            Chest chestRight = ((Chest) doubleChest.getRightSide());
//            boolean changed = false;
            ArrayList<ItemStack> chestContents = new ArrayList<>();
            chestContents.addAll(getContainerItems(null,chestLeft,ContainerType.CHEST,player));
            chestContents.addAll(getContainerItems(null,chestRight,ContainerType.CHEST,player));
            inventory = doubleChest.getInventory();
            if(!chestContents.isEmpty()){
                return chestContents;
            }
        }
        else{
            return null;
        }

        if(data != null){
            List<ItemStack> items = new ArrayList<>();
            if(data.has(Lootin.getKey(uuid),PersistentDataType.STRING)){
                items = ItemSerializer.deserialize(data.get(Lootin.getKey(uuid),PersistentDataType.STRING));
                updatePersistentStorageTypes(data,inventory,items,Lootin.getKey(uuid));
                return items;
            }
            else if(data.has(Lootin.getKey(uuid),DataType.ITEM_STACK_ARRAY)){
                return Arrays.asList(data.get(Lootin.getKey(uuid), DataType.ITEM_STACK_ARRAY));
            }
            else if(data.has(LConstants.DATA_KEY,PersistentDataType.STRING)){
                items = ItemSerializer.deserialize(data.get(LConstants.DATA_KEY,PersistentDataType.STRING));
                updatePersistentStorageTypes(data,inventory,items,LConstants.DATA_KEY);
                if(plugin.getConfig().getBoolean(LConstants.RESET_SEED) && !inventory.isEmpty()){
                    items = Arrays.asList(inventory.getContents());
                }
                inventory.clear();
                return items;
            }
            else if(data.has(LConstants.DATA_KEY, DataType.ITEM_STACK_ARRAY)){
                if(plugin.getConfig().getBoolean(LConstants.RESET_SEED) && !inventory.isEmpty()){
                    items = Arrays.asList(inventory.getContents());
                }
                else{
                    items = Arrays.asList(data.get(LConstants.DATA_KEY, DataType.ITEM_STACK_ARRAY));
                }
                inventory.clear();
                return items;
            }
        }
        ArrayList<ItemStack> chestContents = new ArrayList<>();
        Collections.addAll(chestContents, inventory.getContents());
        setContainerItems(minecart, block, type, chestContents, LConstants.DATA_KEY_STRING);
        inventory.clear();
        return chestContents;
    }

//    this is used to change from old way to store items as String to new way of storing them as ConfigurationSection
    private static void updatePersistentStorageTypes(PersistentDataContainer data,Inventory inv,List<ItemStack> items,NamespacedKey key){
        data.remove(key);
        data.set(key,DataType.ITEM_STACK_ARRAY,items.toArray(new ItemStack[0]));
        if(inv.getHolder() instanceof BlockState){
            ((BlockState)inv.getHolder()).update();
        }
    }

//    private static void fillDoubleLoot(String uuid, Chest chest, ArrayList<ItemStack> items) {
//        PersistentDataContainer data = chest.getPersistentDataContainer();
//        if(data.has(Lootin.getKey(uuid), PersistentDataType.STRING)){
//            items.addAll(ItemSerializer.deserialize(data.get(Lootin.getKey(uuid), PersistentDataType.STRING)));
//            return;
//        }else if(!data.has(LConstants.DATA_KEY, PersistentDataType.STRING)){
//            return;
//        }
//        items.addAll(ItemSerializer.deserialize(data.get(LConstants.DATA_KEY, PersistentDataType.STRING)));
//    }

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
            data.set(Lootin.getKey(key), DataType.ITEM_STACK_ARRAY,items.toArray(new ItemStack[0]));
//            data.set(Lootin.getKey(key), PersistentDataType.STRING, ItemSerializer.serialize(items));
            chest.update();
        }
        else if(type == ContainerType.MINECART){
            StorageMinecart tileCart = (StorageMinecart) minecart;
            data = tileCart.getPersistentDataContainer();
            data.set(Lootin.getKey(key), DataType.ITEM_STACK_ARRAY,items.toArray(new ItemStack[0]));
//            data.set(Lootin.getKey(key), PersistentDataType.STRING, ItemSerializer.serialize(items));

        }
        else if(type == ContainerType.BARREL){
            Barrel barrel = (Barrel) block;
            data = barrel.getPersistentDataContainer();
            data.set(Lootin.getKey(key), DataType.ITEM_STACK_ARRAY,items.toArray(new ItemStack[0]));
//            data.set(Lootin.getKey(key),PersistentDataType.STRING,ItemSerializer.serialize(items));
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