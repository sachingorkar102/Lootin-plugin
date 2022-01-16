package com.github.sachin.lootin.version;

import java.util.Optional;

import com.github.sachin.lootin.utils.LConstants;
import com.github.sachin.lootin.version.lookup.ClassLookupProvider;
import com.github.sachin.lootin.version.lookup.handle.ClassLookup;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.loot.Lootable;

public final class VersionProvider {

    public static final ClassLookupProvider PROVIDER = new ClassLookupProvider();

    private VersionProvider() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void setup() {
        setupAll();
        if(LConstants.SERVER_MINOR_VERSION >= 17) {
            setupRemap();
            setupSearch();
            return;
        }
        setupLegacy();
        setupSearch();
    }

    private static void setupAll() {
        PROVIDER.createCBLookup("CraftBlockEntityState", "block.CraftBlockEntityState").searchMethod("snapshot", "getSnapshot");
        PROVIDER.createCBLookup("CraftEntity", "entity.CraftEntity").searchMethod("handle", "getHandle");
    }

    private static void setupLegacy() {
        PROVIDER.createNMSLookup("TileEntityLootable", "TileEntityLootable");
        PROVIDER.createNMSLookup("EntityMinecartContainer", "EntityMinecartContainer");
    }

    private static void setupRemap() {
        PROVIDER.createNMSLookup("TileEntityLootable", "world.level.block.entity.TileEntityLootable");
        PROVIDER.createNMSLookup("EntityMinecartContainer", "world.entity.vehicle.EntityMinecartContainer");
    }

    private static void setupSearch() {
        Class<?> human = PROVIDER.getNMSClass("EntityHuman");
        PROVIDER.getLookup("TileEntityLootable").searchMethod("fill", "e", human).searchMethod("fill", "d", human);
        PROVIDER.getLookup("EntityMinecartContainer").searchMethod("fill", "e", human).searchMethod("fill", "d", human);
    }

    private static ClassLookup[] require(String... names) {
        ClassLookup[] lookups = new ClassLookup[names.length];
        for(int index = 0; index < names.length; index++) {
            Optional<ClassLookup> lookup = PROVIDER.getOptionalLookup(names[index]);
            if(lookup.isEmpty()){
                throw new IllegalStateException("Lookup " + names[index] + " not found!");
            }
            lookups[index] = lookup.get();
        }
        return lookups;
    }

    public static void fillLoot(Player player, Lootable lootable) {
        if(lootable instanceof BlockState) {
            ClassLookup[] lookup = require("CraftEntity", "CraftBlockEntityState", "TileEntityLootable");
            Object snapshot = lookup[1].run(lootable, "snapshot");
            Object target = lookup[0].run(player, "handle");
            lookup[2].execute(snapshot, "fill", target);
            return;
        }
        ClassLookup[] lookup = require("CraftEntity", "EntityMinecartContainer");
        Object handle = lookup[0].run(lootable, "handle");
        Object target = lookup[0].run(player, "handle");
        lookup[1].execute(handle, "fill", target);
    }   
    
}
