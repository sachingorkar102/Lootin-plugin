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
import org.bukkit.event.Listener;
import org.bukkit.event.world.AsyncStructureGenerateEvent;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BlockTransformer;
import org.bukkit.util.EntityTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StructureGenerateListener extends BaseListener {

    @EventHandler
    public void onStrucGenerate(AsyncStructureGenerateEvent e){
        e.setBlockTransformer(LConstants.IDENTITY_KEY,new StructureTransformer(e.getWorld().getUID()));
        e.setEntityTransformer(LConstants.IDENTITY_KEY,new StructureTransformer(e.getWorld().getUID()));
    }

    private static final class StructureTransformer implements BlockTransformer, EntityTransformer {

        private final UUID world;

        public StructureTransformer(UUID world){
            this.world = world;
        }


        @Override
        public BlockState transform(@NotNull LimitedRegion region, int x, int y, int z, @NotNull BlockState block, @NotNull BlockTransformer.TransformationState state) {
            if(Lootin.getPlugin().isBlackListWorld(world)) return block;
            if(block instanceof Lootable){
                Lootable lootable = (Lootable) block;
                if(lootable.getLootTable() == null || Lootin.getPlugin().getBlackListStructures().contains(lootable.getLootTable().getKey())) {
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

        @Override
        public boolean transform(@NotNull LimitedRegion region, int x, int y, int z, @NotNull Entity entity, boolean allowedToSpawn) {
            if(Lootin.getPlugin().isBlackListWorld(world)) return allowedToSpawn;
            if(entity.getType()== EntityType.ITEM_FRAME){
                if(entity.getWorld().getEnvironment() == World.Environment.THE_END && Lootin.getPlugin().getConfig().getBoolean(LConstants.PER_PLAYER_ELYTRA_ITEM_FRAME)){
                    ItemFrame frame = (ItemFrame) entity;
                    if(frame.getItem() != null && frame.getItem().getType()==Material.ELYTRA){
                        frame.getPersistentDataContainer().set(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER, 1);
                    }
                }
            }
            if(entity.getType()==EntityType.MINECART_CHEST){
                StorageMinecart minecart = (StorageMinecart) entity;
                if (!ChestUtils.isLootinContainer(minecart, null, ContainerType.MINECART)){
                    if(minecart.getLootTable() == null || Lootin.getPlugin().getBlackListStructures().contains(minecart.getLootTable().getKey())) {
                        return allowedToSpawn;
                    }
                    ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);

                }
            }
            return allowedToSpawn;
        }
    }
}
