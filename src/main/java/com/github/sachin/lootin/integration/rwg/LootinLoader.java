package com.github.sachin.lootin.integration.rwg;

import java.util.Arrays;
import java.util.List;

import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

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

  public LootinLoader(RealisticWorldGenerator api, CompatibilityAddon addon) {
    super(api, addon, LootinAddon.NAMESPACE);
  }


  @Override
  public IBlockData load(IBlockAccess arg0, BlockState state, BlockData data) {
    BlockStateEditor editor = BlockStateEditor.of(data.getAsString());
    CustomBlockData customData = null;
    if(editor.getId().equalsIgnoreCase("barrel")) {
      customData = new CustomBlockData(LootinAddon.NAMESPACE, editor.getId());
      Barrel barrel = (Barrel) state;
      if(barrel.getPersistentDataContainer().has(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)){
        customData.getProperties().set(IProperty.of("facing", editor.get("facing")));
        customData.getProperties().set(IProperty.of("loottable", barrel.getPersistentDataContainer().get(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)));
        return customData;
      }
    }
    if(editor.getId().equalsIgnoreCase("chest")) {
      Chest chest = (Chest) state;
      PersistentDataContainer persistent = chest.getPersistentDataContainer();

      if(persistent.has(LConstants.RWG_CONTAINER_KEY, PersistentDataType.STRING) && persistent.has(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING)){
        String loottable = persistent.get(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING);
        String containerType = persistent.get(LConstants.RWG_CONTAINER_KEY, PersistentDataType.STRING);
        String facing = editor.get("facing");
        if(containerType.equals("CHEST")){
          customData = new CustomBlockData(LootinAddon.NAMESPACE, editor.getId());
          customData.getProperties().set(IProperty.of("type", editor.get("type")));
          customData.getProperties().set(IProperty.of("facing", facing));
        }
        if(containerType.equals("MINECART")){
          customData = new CustomBlockData(LootinAddon.NAMESPACE, "minecart");
          String rail = Material.RAIL.toString();
          Inventory inv = chest.getBlockInventory();
          for(Material r : VALID_RAILS){
            if(inv.contains(r)){
              rail = r.toString();
              break;
            }
          }
          customData.getProperties().set(IProperty.of("rail_type", rail));
          if(facing.equals("east") || facing.equals("west")){
            customData.getProperties().set(IProperty.of("shape","east_west"));
          }
          else{
            customData.getProperties().set(IProperty.of("shape","north_south"));
          }
        }
        customData.getProperties().set(IProperty.of("loottable", loottable));
        return customData;
      } 
    }
    return null;
  }

  
}
