package com.github.sachin.lootin.integration.rwg;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
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
  public IBlockData load(IBlockAccess access, Block block, BlockData data) {
    
    IBlockData iBlockData = new CustomBlockData(LootinAddon.NAMESPACE, "chest");
    
    return iBlockData;
  }
  
}
