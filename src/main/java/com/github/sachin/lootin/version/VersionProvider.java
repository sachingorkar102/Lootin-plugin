package com.github.sachin.lootin.version;

import java.util.Optional;

import com.github.sachin.lootin.utils.LConstants;
import com.github.sachin.lootin.version.lookup.ClassLookupProvider;
import com.github.sachin.lootin.version.lookup.handle.ClassLookup;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
            return;
        }
        setupLegacy();
    }

    private static void setupAll() {
        PROVIDER.createCBLookup("CraftBlockEntityState", "block.CraftBlockEntityState").searchMethod("snapshot", "getSnapshot");
        PROVIDER.createCBLookup("CraftEntity", "entity.CraftEntity").searchMethod("handle", "getHandle");
    }

    private static void setupLegacy() {
        PROVIDER.createNMSLookup("TileEntityLootable", "TileEntityLootable").searchMethod("fill", "d", PROVIDER.getNMSClass("EntityHuman"));
    }

    private static void setupRemap() {
        PROVIDER.createNMSLookup("TileEntityLootable", "world.level.block.entity.TileEntityLootable").searchMethod("fill", "e", PROVIDER.getNMSClass("world.entity.player.EntityHuman"));
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
        ClassLookup[] lookup = require("CraftEntity", "CraftBlockEntityState", "TileEntityLootable");
        Object snapshot = lookup[1].run(lootable, "snapshot");
        Object target = lookup[0].run(player, "handle");
        lookup[2].run(snapshot, "fill", target);
    }   
    
}
