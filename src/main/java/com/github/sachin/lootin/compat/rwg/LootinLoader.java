package com.github.sachin.lootin.compat.rwg;

import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Material;
import org.bukkit.block.TileState;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.block.BlockStateEditor;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockAccess;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.block.impl.CustomBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityAddon;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityBlockLoader;
import net.sourcewriters.spigot.rwg.legacy.api.data.property.IProperty;

public class LootinLoader extends CompatibilityBlockLoader {

  public static final List<Material> VALID_RAILS = Arrays.asList(Material.RAIL,Material.POWERED_RAIL,Material.DETECTOR_RAIL,Material.ACTIVATOR_RAIL);
  public static final List<String> FRAME_ROTATIONS = Arrays.asList("north", "east", "south", "west");

  public LootinLoader(RealisticWorldGenerator api, CompatibilityAddon addon) {
    super(api, addon, LootinAddon.NAMESPACE);
  }

  @Override
  public IBlockData load(IBlockAccess access, BlockState state, BlockData data) {
    BlockStateEditor editor = BlockStateEditor.of(data.getAsString());
    String id = editor.getId().toLowerCase();
    switch(id) {
      case "barrel":
        return loadBarrel(editor, state);
      case "chest":
        return loadChest(editor, state);
      case "furnace":
        return loadMinecart(editor, state);
      case "oak_sign":
      case "oak_wall_sign":
        return loadElytra(editor, state);
      default:
        return null;
    }
  }

  private IBlockData loadBarrel(BlockStateEditor data, BlockState state) {
    PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();
    if(!isLootinBlock(container)){
      return null;
    }
    CustomBlockData blockData = new CustomBlockData(LootinAddon.NAMESPACE, data.getId());
    blockData.getProperties().set(IProperty.of("facing", data.get("facing")));
    if(container.has(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)){
      blockData.getProperties().set(IProperty.of("loottable", container.get(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)));
    }
    return blockData;
  }

  private IBlockData loadChest(BlockStateEditor data, BlockState state) {
    PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();
    if(!isLootinBlock(container)){
      return null;
    }
    CustomBlockData blockData = new CustomBlockData(LootinAddon.NAMESPACE, data.getId());
    blockData.getProperties().set(IProperty.of("type", data.get("type")));
    blockData.getProperties().set(IProperty.of("facing", data.get("facing")));
    if(container.has(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)){
      blockData.getProperties().set(IProperty.of("loottable", container.get(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)));
    }
    return blockData;
  }

  private IBlockData loadMinecart(BlockStateEditor data, BlockState state) {
    Container containerState = ((Container) state);
    PersistentDataContainer container = containerState.getPersistentDataContainer();
    if(!isLootinBlock(container)){
      return null;
    }
    CustomBlockData blockData = new CustomBlockData(LootinAddon.NAMESPACE, "minecart");
    ItemStack itemStack = containerState.getInventory().getItem(0);
    if(itemStack != null && VALID_RAILS.contains(itemStack.getType())) {
      blockData.getProperties().set(IProperty.of("rail_type", itemStack.getType().toString()));
    }
    String facing = data.get("facing");
    blockData.getProperties().set(IProperty.of("shape", (facing.equals("east") ||facing.equals("west")) ? "east_west" : "north_south"));
    if(container.has(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)){
      blockData.getProperties().set(IProperty.of("loottable", container.get(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)));
    }
    return blockData;
  }

  private IBlockData loadElytra(BlockStateEditor data, BlockState state) {
    PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();
    if(!isLootinBlock(container)){
      return null;
    }
    CustomBlockData blockData = new CustomBlockData(LootinAddon.NAMESPACE, "elytra");
    blockData.getProperties().set(IProperty.of("glow", data.has("lit") && data.get("lit").equalsIgnoreCase("true")));
    if(!data.getId().contains("wall")) {
      blockData.getProperties().set(IProperty.of("facing", (byte) 6));
      return blockData;
    }
    blockData.getProperties().set(IProperty.of("facing", (byte) FRAME_ROTATIONS.indexOf(data.get("facing").toLowerCase())));
    return blockData;
  }

  public boolean isLootinBlock(PersistentDataContainer data) {
    return data.has(LConstants.RWG_IDENTITY_KEY, PersistentDataType.BYTE);
  }


}
