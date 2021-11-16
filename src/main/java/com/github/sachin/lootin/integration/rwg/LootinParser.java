package com.github.sachin.lootin.integration.rwg;

import com.github.sachin.lootin.integration.rwg.util.ChestSide;
import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockAccess;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.block.impl.CustomBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityAddon;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityBlockParser;
import net.sourcewriters.spigot.rwg.legacy.api.data.property.IProperty;

public class LootinParser extends CompatibilityBlockParser {

  public LootinParser(RealisticWorldGenerator api, CompatibilityAddon addon) {
    super(api, addon, LootinAddon.NAMESPACE);
  }

  @Override
  public IBlockData parse(IBlockAccess access, NbtCompound compound) {
    String id = compound.getString("id");
    if(id.equalsIgnoreCase("barrel")) {
      CustomBlockData data = new CustomBlockData(LootinAddon.NAMESPACE, compound.getString("id"));
      if(compound.hasKey("properties", NbtType.COMPOUND)) {
        NbtCompound properties = compound.getCompound("properties");
        if(properties.hasKey("loottable", NbtType.STRING)) {
          data.getProperties().set(IProperty.of("loottable", properties.getString("loottable")));
        }
      }
      return data;
    }
    if(id.equalsIgnoreCase("chest")) {
      CustomBlockData data = new CustomBlockData(LootinAddon.NAMESPACE, compound.getString("id"));
      if(compound.hasKey("properties", NbtType.COMPOUND)) {
        NbtCompound properties = compound.getCompound("properties");
        if(properties.hasKey("loottable", NbtType.STRING)) {
          data.getProperties().set(IProperty.of("loottable", properties.getString("loottable")));
        }
        if(properties.hasKey("side", NbtType.BYTE)) {
          data.getProperties().set(IProperty.of("side", ChestSide.of(properties.getByte("side"))));
        }
      }
      return data;
    }
    return null;
  }
  
}
