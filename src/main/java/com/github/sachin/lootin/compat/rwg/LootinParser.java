package com.github.sachin.lootin.compat.rwg;

import java.util.Arrays;
import java.util.List;

import com.syntaxphoenix.syntaxapi.nbt.NbtCompound;
import com.syntaxphoenix.syntaxapi.nbt.NbtType;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockAccess;
import net.sourcewriters.spigot.rwg.legacy.api.block.IBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.block.impl.CustomBlockData;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityBlockParser;
import net.sourcewriters.spigot.rwg.legacy.api.data.property.IProperties;
import net.sourcewriters.spigot.rwg.legacy.api.data.property.IProperty;

public final class LootinParser extends CompatibilityBlockParser {

    public static final List<String> VALID_IDS = Arrays.asList("chest", "barrel", "minecart", "elytra");

    public LootinParser(RealisticWorldGenerator api, LootinAddon addon) {
        super(api, addon, LootinAddon.NAMESPACE);
    }

    @Override
    public IBlockData parse(IBlockAccess access, NbtCompound compound) {
        String id = compound.getString("id").toLowerCase();
        if(!VALID_IDS.contains(id)) {
            return null;
        }
        CustomBlockData blockData = new CustomBlockData(LootinAddon.NAMESPACE, id);
        switch(id){
            case "chest":
                parseChest(blockData, compound.getCompound("properties"));
                break;
            case "barrel":
                parseBarrel(blockData, compound.getCompound("properties"));
                break;
            case "minecart":
                parseMinecart(blockData, compound.getCompound("properties"));
                break;
            case "elytra":
                parseElytra(blockData, compound.getCompound("properties"));
                break;
        }
        return blockData;
    }

    private void parseChest(CustomBlockData blockData, NbtCompound compound) {
        if(compound == null) {
            return;
        }
        IProperties properties = blockData.getProperties();
        if(compound.hasKey("type", NbtType.STRING)){
            properties.set(IProperty.of("type", compound.getString("type")));
        }
        if(compound.hasKey("facing", NbtType.STRING)){
            properties.set(IProperty.of("facing", compound.getString("facing")));
        }
        if(compound.hasKey("loottable", NbtType.STRING)){
            properties.set(IProperty.of("loottable", compound.getString("loottable")));
        }
    }

    private void parseBarrel(CustomBlockData blockData, NbtCompound compound) {
        if(compound == null) {
            return;
        }
        IProperties properties = blockData.getProperties();
        if(compound.hasKey("facing", NbtType.STRING)){
            properties.set(IProperty.of("facing", compound.getString("facing")));
        }
        if(compound.hasKey("loottable", NbtType.STRING)){
            properties.set(IProperty.of("loottable", compound.getString("loottable")));
        }
    }

    private void parseElytra(CustomBlockData blockData, NbtCompound compound) {
        if(compound == null) {
            return;
        }
        IProperties properties = blockData.getProperties();
        if(compound.hasKey("glow", NbtType.BYTE)){
            properties.set(IProperty.of("glow", compound.getBoolean("glow")));
        }
        if(compound.hasKey("facing", NbtType.BYTE)){
            properties.set(IProperty.of("facing", compound.getByte("facing")));
        }
    }

    private void parseMinecart(CustomBlockData blockData, NbtCompound compound) {
        if(compound == null) {
            return;
        }
        IProperties properties = blockData.getProperties();
        if(compound.hasKey("shape", NbtType.STRING)){
            properties.set(IProperty.of("shape", compound.getString("shape")));
        }
        if(compound.hasKey("loottable", NbtType.STRING)){
            properties.set(IProperty.of("loottable", compound.getString("loottable")));
        }
        if(compound.hasKey("rail_type", NbtType.STRING)){
            properties.set(IProperty.of("rail_type", compound.getString("rail_type")));
        }
    }
    
    

}
