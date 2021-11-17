package com.github.sachin.lootin.integration.rwg;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;

import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.block.BlockStateEditor;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockAccess;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.block.impl.CustomBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityAddon;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityBlockLoader;

public class LootinLoader extends CompatibilityBlockLoader {

  public LootinLoader(RealisticWorldGenerator api, CompatibilityAddon addon) {
    super(api, addon, LootinAddon.NAMESPACE);
  }

  @Override
  public IBlockData load(IBlockAccess access, BlockState blockState, BlockData data) {
    BlockStateEditor editor = BlockStateEditor.of(data.getAsString());
    if(editor.getId().equalsIgnoreCase("barrel") && ChestUtils.isLootinContainer(null, blockState, ContainerType.BARREL)) {
      // TODO: Load loottable
      return new CustomBlockData(LootinAddon.NAMESPACE, editor.getId());
    }
    if(editor.getId().equalsIgnoreCase("chest") && ChestUtils.isLootinContainer(null, blockState, ChestUtils.isDoubleChest(blockState) ? ContainerType.DOUBLE_CHEST : ContainerType.CHEST)) {
      // TODO: Load loottable
      // TODO: Load direction and double chest side
      return new CustomBlockData(LootinAddon.NAMESPACE, editor.getId());
    }
    return null;
  }
  
}
