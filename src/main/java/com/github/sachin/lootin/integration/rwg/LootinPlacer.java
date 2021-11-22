package com.github.sachin.lootin.integration.rwg;

import com.github.sachin.lootin.integration.rwg.util.ChestSide;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.google.common.base.Enums;
import com.syntaxphoenix.syntaxapi.random.NumberGeneratorType;
import com.syntaxphoenix.syntaxapi.random.RandomNumberGenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.block.BlockStateEditor;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockAccess;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.chest.IChestStorage;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityAddon;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityBlockPlacer;
import net.sourcewriters.spigot.rwg.legacy.api.data.property.IProperties;
import net.sourcewriters.spigot.rwg.legacy.api.version.util.MinecraftVersion;
import net.sourcewriters.spigot.rwg.legacy.api.version.util.ServerVersion;

public class LootinPlacer extends CompatibilityBlockPlacer {

  private final IChestStorage storage;
  private final IBlockAccess blockAccess;

  public LootinPlacer(RealisticWorldGenerator api, CompatibilityAddon addon) {
    super(api, addon, LootinAddon.NAMESPACE);
    this.storage = api.getChestStorage();
    this.blockAccess = api.getBlockAccess();
  }

  @Override
  public boolean placeBlock(Location location, Block block, IBlockData data, RandomNumberGenerator random,
      MinecraftVersion minecraft, ServerVersion server) {
    
    String id = data.getId();

    Inventory inventory = null;
    if(id.equalsIgnoreCase("barrel")) {
      BlockStateEditor editor = BlockStateEditor.of("minecraft:" + id);
      IProperties properties = data.getProperties();
      BlockFace facing = Enums.getIfPresent(BlockFace.class, properties.find("facing").cast(String.class).getValueOr("UP").toUpperCase()).get();
      editor.put("facing", facing.name().toLowerCase());
      block.setBlockData(blockAccess.dataOf(editor.asBlockData()).asBukkit());
      block.getState().update(true);
      BlockState state = block.getState();
      if(state instanceof Barrel) {
        inventory = ((Barrel) state).getInventory();
      }
      ChestUtils.setLootinContainer(null, state, ContainerType.BARREL);
    }
    if(id.equalsIgnoreCase("chest")) {
      BlockStateEditor editor = BlockStateEditor.of("minecraft:" + id);
      IProperties properties = data.getProperties();
      ChestSide side = Enums.getIfPresent(ChestSide.class, properties.find("type").cast(String.class).getValueOr("SINGLE").toUpperCase()).get();
      BlockFace facing = Enums.getIfPresent(BlockFace.class, properties.find("facing").cast(String.class).getValueOr("WEST").toUpperCase()).get();
      editor.put("type", side.name().toLowerCase());
      editor.put("facing", facing.name().toLowerCase());
      block.setBlockData(blockAccess.dataOf(editor.asBlockData()).asBukkit());
      block.getState().update(true);
      BlockState state = block.getState();
      if(state instanceof Chest) {
        inventory = ((org.bukkit.block.Chest) state).getBlockInventory();
      }
      ChestUtils.setLootinContainer(null, state, side != ChestSide.SINGLE ? ContainerType.DOUBLE_CHEST : ContainerType.CHEST);
    }
    if(id.equalsIgnoreCase("minecart")){
      IProperties properties = data.getProperties();
      String shape = properties.find("shape").cast(String.class).getValueOr("east_west");
      Material rail = Enums.getIfPresent(Material.class, properties.find("rail_type").cast(String.class).getValueOr("RAIL").toUpperCase()).get();
      BlockStateEditor editor = BlockStateEditor.of("minecraft:" + rail.toString().toLowerCase());
      editor.put("shape", shape);
      block.setBlockData(blockAccess.dataOf(editor.asBlockData()).asBukkit());
      block.getState().update(true);
      StorageMinecart minecart = block.getWorld().spawn(block.getLocation().add(0.5, 0, 0.5), StorageMinecart.class);
      inventory = minecart.getInventory();
      ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);
    }

    if(inventory == null) {
      return false;
    }
    String chestName = data.getProperties().find("loottable").cast(String.class).getValueOr("Lootin");
    storage.fillInventory(chestName, inventory, NumberGeneratorType.MURMUR.create(random.nextLong()));
    return true;
  }
  
}
