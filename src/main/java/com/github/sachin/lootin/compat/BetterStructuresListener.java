package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;
import com.magmaguy.betterstructures.api.BuildPlaceEvent;
import com.magmaguy.betterstructures.api.ChestFillEvent;
import com.magmaguy.betterstructures.buildingfitter.FitAnything;
import com.magmaguy.betterstructures.buildingfitter.util.LocationProjector;
import com.magmaguy.betterstructures.config.generators.GeneratorConfig;
import com.magmaguy.betterstructures.config.generators.GeneratorConfigFields;
import com.magmaguy.betterstructures.schematics.SchematicContainer;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Iterator;

public class BetterStructuresListener implements Listener {


    @EventHandler
    public void onChestFill(ChestFillEvent e){
        if(e.getContainer() instanceof Chest){

            ChestUtils.setLootinContainer(null,e.getContainer(), ContainerType.CHEST);
        }
    }

    @EventHandler
    public void onStructurePlace(BuildPlaceEvent e){
        FitAnything fitAnything = e.getFitAnything();
        SchematicContainer schematicContainer = fitAnything.getSchematicContainer();
        if (schematicContainer.getGeneratorConfigFields().getChestContents() != null) {
            Iterator var1 = schematicContainer.getChestLocations().iterator();

            while (var1.hasNext()) {
                Vector chestPosition = (Vector) var1.next();
                Location chestLocation = LocationProjector.project(fitAnything.getLocation(), fitAnything.getSchematicOffset(), chestPosition);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(chestLocation.getBlock().getState() instanceof Chest){
                            Chest container = (Chest) chestLocation.getBlock().getState();
                            container.getPersistentDataContainer().set(LConstants.BETTER_STRUC_KEY, PersistentDataType.STRING,schematicContainer.getGeneratorConfigFields().getFilename());
                            container.update();
                        }
                    }
                }.runTaskLater(Lootin.getPlugin(),1);
            }
        }
    }

    public static void refillChest(Chest chest){
        String fileName = chest.getPersistentDataContainer().get(LConstants.BETTER_STRUC_KEY,PersistentDataType.STRING);
        if(fileName != null){
            if(GeneratorConfig.getGeneratorConfigurations().containsKey(fileName)){
                GeneratorConfigFields generatorConfigFields = GeneratorConfig.getGeneratorConfigurations().get(fileName);
                if(generatorConfigFields != null && generatorConfigFields.getChestContents() != null){
                    chest.getSnapshotInventory().clear();
                    generatorConfigFields.getChestContents().rollChestContents(chest);
                }
            }
        }
    }
}
