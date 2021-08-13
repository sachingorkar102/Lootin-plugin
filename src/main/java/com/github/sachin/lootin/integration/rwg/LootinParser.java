package com.github.sachin.lootin.integration.rwg;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockAccess;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.block.impl.CustomBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityAddon;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityBlockParser;
import net.sourcewriters.spigot.rwg.legacy.api.data.property.IProperty;
import net.sourcewriters.spigot.rwg.legacy.shaded.syntaxapi.nbt.NbtCompound;
import net.sourcewriters.spigot.rwg.legacy.shaded.syntaxapi.nbt.NbtType;

public class LootinParser extends CompatibilityBlockParser {

  public LootinParser(RealisticWorldGenerator api, CompatibilityAddon addon) {
    super(api, addon, LootinAddon.NAMESPACE);
  }

  @Override
  public IBlockData parse(IBlockAccess access, NbtCompound compound) {
    // 
    // TODO: Add id validation - does id exist?
    //
    CustomBlockData data = new CustomBlockData(LootinAddon.NAMESPACE, compound.getString("id"));
    if(compound.hasKey("properties", NbtType.COMPOUND)) {
      NbtCompound properties = compound.getCompound("properties");
      if(properties.hasKey("chest", NbtType.STRING)) {
        data.getProperties().set(IProperty.of("chest", properties.getString("chest")));
      }
    }
    return data;
  }
  
}
