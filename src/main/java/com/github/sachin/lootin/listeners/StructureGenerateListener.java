package com.github.sachin.lootin.listeners;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.AsyncStructureGenerateEvent;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BlockTransformer;
import org.bukkit.util.EntityTransformer;
import org.jetbrains.annotations.NotNull;


public class StructureGenerateListener extends BaseListener {


    private final BlockTransformer CONTAINER_TRANSFORMER = this::containerTransformer;
    private final EntityTransformer ITEMFRAME_TRANSFORMER = this::itemFrameTransformer;
    private final EntityTransformer MINECART_TRANSFORMER = this::minecartTransformer;
    @EventHandler
    public void onStrucGenerate(AsyncStructureGenerateEvent e){
        if(plugin.isBlackListWorld(e.getWorld())) return;
        e.setBlockTransformer(LConstants.TRANSFORMER_CHEST_KEY, CONTAINER_TRANSFORMER);
        if(e.getWorld().getEnvironment()== World.Environment.NORMAL){
            e.setEntityTransformer(LConstants.TRANSFORMER_MINECART_KEY, MINECART_TRANSFORMER);
        }
        if(plugin.getConfig().getBoolean(LConstants.PER_PLAYER_ELYTRA_ITEM_FRAME) && e.getWorld().getEnvironment()== World.Environment.THE_END){
            e.setEntityTransformer(LConstants.TRANSFORMER_ITEMFRAME_KEY, ITEMFRAME_TRANSFORMER);
        }
    }

    public BlockState containerTransformer(@NotNull LimitedRegion region, int x, int y, int z, @NotNull BlockState block, @NotNull BlockTransformer.TransformationState state) {
        if(block instanceof Lootable){
            Lootable lootable = (Lootable) block;

            if(lootable.getLootTable() == null || Lootin.getPlugin().isBlackListedLootable(lootable.getLootTable(),block.getWorld())) {
                return block;
            }
            boolean isLootin = false;
            ContainerType container;
            if (ChestUtils.isChest(block.getType())) {

                isLootin = ChestUtils.isLootinContainer(null, block, container = (ChestUtils.isDoubleChest(block) ? ContainerType.DOUBLE_CHEST : ContainerType.CHEST));
            } else if (block.getType() == Material.BARREL) {
                isLootin = ChestUtils.isLootinContainer(null, block, container = ContainerType.BARREL);
            }
            else{return block;}
            if(!isLootin){
                ChestUtils.setLootinContainer(null,block,container);
            }
        }
        return block;
    }

    public boolean itemFrameTransformer(@NotNull LimitedRegion region, int x, int y, int z, @NotNull Entity entity, boolean allowedToSpawn) {
        if(entity.getType()== EntityType.ITEM_FRAME){
            ItemFrame frame = (ItemFrame) entity;
            frame.getPersistentDataContainer().set(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER, 1);

        }
        return allowedToSpawn;
    }

    public boolean minecartTransformer(@NotNull LimitedRegion region, int x, int y, int z, @NotNull Entity entity, boolean allowedToSpawn) {
        if(entity.getType()==EntityType.MINECART_CHEST){
            StorageMinecart minecart = (StorageMinecart) entity;
            if (!ChestUtils.isLootinContainer(minecart, null, ContainerType.MINECART)){
                if(minecart.getLootTable() == null || Lootin.getPlugin().isBlackListedLootable(minecart.getLootTable(),minecart.getWorld())) {
                    return allowedToSpawn;
                }
                ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);

            }
        }
        return allowedToSpawn;
    }
}
