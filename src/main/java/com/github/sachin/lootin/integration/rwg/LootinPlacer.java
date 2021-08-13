package com.github.sachin.lootin.integration.rwg;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.chest.IChestStorage;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityAddon;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityBlockPlacer;
import net.sourcewriters.spigot.rwg.legacy.api.version.util.MinecraftVersion;
import net.sourcewriters.spigot.rwg.legacy.api.version.util.ServerVersion;
import net.sourcewriters.spigot.rwg.legacy.shaded.syntaxapi.random.NumberGeneratorType;
import net.sourcewriters.spigot.rwg.legacy.shaded.syntaxapi.random.RandomNumberGenerator;

public class LootinPlacer extends CompatibilityBlockPlacer {

  private final IChestStorage storage;

  public LootinPlacer(RealisticWorldGenerator api, CompatibilityAddon addon) {
    super(api, addon, LootinAddon.NAMESPACE);
    this.storage = api.getChestStorage();
  }

  @Override
  public boolean placeBlock(Location location, Block block, IBlockData data, RandomNumberGenerator random,
      MinecraftVersion minecraft, ServerVersion server) {
    
    //
    // TODO: Implement block placement or Entity spawning logic
    //

    Inventory inventory = null; // TODO: Get inventory
    String chestName = data.getProperties().find("chest").cast(String.class).getValueOr("Lootin");
    storage.fillInventory(chestName, inventory, NumberGeneratorType.MURMUR.create(random.nextLong()));
    return false;
  }
  
}
