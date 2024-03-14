package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.LConstants;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WGFlag {

    private Lootin plugin;
    private WorldGuard WGinstance;
    private FlagRegistry registry;

    private StateFlag flag;

    public WGFlag(){
        this.plugin = Lootin.getPlugin();
        this.WGinstance = WorldGuard.getInstance();
        this.registry = WGinstance.getFlagRegistry();
    }

    public void registerFlag(){
        this.flag = new StateFlag(LConstants.WG_FLAG_NAME,plugin.getConfig().getBoolean("default-worldguard-flag-value",true));
        try {
            registry.register(flag);
            plugin.getLogger().info("Worldguard flag: "+LConstants.WG_FLAG_NAME+ " registered successfully");
        } catch (FlagConflictException e) {
            plugin.getLogger().warning("A flag with the name \"" + flag.getName() + "\" already exists and could not be registered.");
            e.printStackTrace();
        }
    }

    public boolean queryFlag(Player player){
        return queryFlag(player, player.getLocation());
    }

    public boolean queryFlag(Player player, Location loc){
        if(player.isOp()) return true;
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer regionContainer = WGinstance.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(localPlayer.getWorld());
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));

        return regionSet.testState(localPlayer, flag);
    }
}
