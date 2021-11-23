package com.github.sachin.lootin.integration.rwg;

import com.google.common.base.Enums;
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
        if(properties.hasKey("facing",NbtType.STRING)){
          data.getProperties().set(IProperty.of("facing", properties.getString("facing")));
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
        if(properties.hasKey("type", NbtType.STRING)) {
          data.getProperties().set(IProperty.of("type", properties.getString("type")));
        }
        if(properties.hasKey("facing",NbtType.STRING)){
          data.getProperties().set(IProperty.of("facing", properties.getString("facing")));
        }
      }
      return data;
    }
    if(id.equalsIgnoreCase("minecart")) {
      CustomBlockData data = new CustomBlockData(LootinAddon.NAMESPACE, compound.getString("id"));
      if(compound.hasKey("properties", NbtType.COMPOUND)) {
        NbtCompound properties = compound.getCompound("properties");
        if(properties.hasKey("loottable", NbtType.STRING)) {
          data.getProperties().set(IProperty.of("loottable", properties.getString("loottable")));
        }
        if(properties.hasKey("shape", NbtType.STRING)) {
          data.getProperties().set(IProperty.of("shape", properties.getString("shape")));
        }
        if(properties.hasKey("rail_type",NbtType.STRING)){
          data.getProperties().set(IProperty.of("rail_type", properties.getString("rail_type")));
        }
      }
      return data;
    }
    return null;
  }
  
}
