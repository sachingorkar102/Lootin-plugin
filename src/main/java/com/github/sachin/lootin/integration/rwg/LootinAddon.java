package com.github.sachin.lootin.integration.rwg;

import org.bukkit.plugin.Plugin;

import net.sourcewriters.spigot.rwg.legacy.api.RealisticWorldGenerator;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.CompatibilityAddon;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.ICompatibilityManager;
import net.sourcewriters.spigot.rwg.legacy.api.compatibility.IPluginPackage;

public final class LootinAddon extends CompatibilityAddon {

  public static final String NAMESPACE = "lootin";

  public LootinAddon(ICompatibilityManager manager, Plugin owner, IPluginPackage target) {
    super(manager, owner, target);
  }

  @Override
  protected void onEnable(RealisticWorldGenerator api, IPluginPackage target) throws Exception {
    new LootinPlacer(api, this);
    new LootinParser(api, this);
    new LootinLoader(api, this);
  }

  @Override
  protected void onDisable(RealisticWorldGenerator api, IPluginPackage target) throws Exception {
    
  }

}