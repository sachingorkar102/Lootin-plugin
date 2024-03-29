package com.github.sachin.lootin.version;

import java.util.Optional;
import java.util.Random;

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
        if (LConstants.SERVER_REMAPPED) {
            setupRemap();
            setupSearch();
            return;
        }
        setupLegacy();
        setupSearch();
    }

    /*
     * Legacy stuff
     */

    private static void setupLegacy() {
        PROVIDER.createNMSLookup("EntityHuman", "EntityHuman");
        PROVIDER.createNMSLookup("TileEntityLootable", "TileEntityLootable");
        PROVIDER.createNMSLookup("EntityMinecartContainer", "EntityMinecartContainer");
    }

    /*
     * Remap stuff
     */

    private static void setupRemap() {
        PROVIDER.createNMSLookup("EntityHuman", "world.entity.player.EntityHuman");
        PROVIDER.createNMSLookup("TileEntityLootable", "world.level.block.entity.TileEntityLootable");
        PROVIDER.createNMSLookup("EntityMinecartContainer", "world.entity.vehicle.EntityMinecartContainer");
    }

    /*
     * All stuff
     */

    private static void setupAll() {
        PROVIDER.createCBLookup("CraftBlockEntityState", "block.CraftBlockEntityState").searchMethod("handle",
                "getTileEntity");
        PROVIDER.createCBLookup("CraftEntity", "entity.CraftEntity").searchMethod("handle", "getHandle");
    }

    private static void setupSearch() {
        Class<?> human = PROVIDER.getLookup("EntityHuman").getOwner();
        PROVIDER.getLookup("TileEntityLootable").searchMethod("fill", "e", human).searchMethod("fill", "d", human);
        PROVIDER.getLookup("EntityMinecartContainer").searchMethod("fill", "e", human).searchMethod("fill", "d", human);
    }

    private static ClassLookup[] require(String... names) {
        ClassLookup[] lookups = new ClassLookup[names.length];
        for (int index = 0; index < names.length; index++) {
            Optional<ClassLookup> lookup = PROVIDER.getOptionalLookup(names[index]);
            if (lookup.isPresent()) {
                throw new IllegalStateException("Lookup " + names[index] + " not found!");
            }
            lookups[index] = lookup.get();
        }
        return lookups;
    }

    public static void fillLoot(Player player, Lootable lootable) {
        if (lootable instanceof BlockState) {
            ClassLookup[] lookup = require("CraftEntity", "CraftBlockEntityState", "TileEntityLootable");
            Object handle = lookup[1].run(lootable, "handle");
            Object target = lookup[0].run(player, "handle");
            lookup[2].execute(handle, "fill", target);
            return;
        }
        ClassLookup[] lookup = require("CraftEntity", "EntityMinecartContainer");
        Object handle = lookup[0].run(lootable, "handle");
        Object target = lookup[0].run(player, "handle");
        lookup[1].execute(handle, "fill", target);
    }

}
