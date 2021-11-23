package com.github.sachin.lootin.commands;

import java.util.Arrays;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.integration.rwg.RWGCompat;
import com.github.sachin.lootin.integration.rwg.listener.RwgInventoryListener;
import com.github.sachin.lootin.integration.rwg.util.inventory.RwgInventory;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("lootin")
public class Commands extends BaseCommand{

    private Lootin plugin;

    public Commands(Lootin plugin){
        this.plugin = plugin;
    }


    @Subcommand("reload")
    public void onCommand(CommandSender sender){
        if(sender.hasPermission("lootin.command.reload")){
            plugin.reloadConfigs();
            sender.sendMessage(plugin.getMessage(LConstants.RELOADED,null));
        }
        else{
            sender.sendMessage(plugin.getMessage(LConstants.NO_PERMISSION,null));
        }
    }

    @Subcommand("rwg give")
    @CommandCompletion("CHEST|BARREL|MINECART @rwgloottables")
    public void onRwgChestCommand(Player player,String[] args){
        if(!player.hasPermission("lootin.command.rwg")){
            player.sendMessage(plugin.getMessage(LConstants.NO_PERMISSION,null));
            return;
        }
        if(plugin.rwgCompat == null){
            player.sendMessage(plugin.getPrefix()+ChatColor.RED+"You need Realistic World Generator plugin installed to use this command");
            return;
        }
        if(args.length < 2){
            player.sendMessage(plugin.getPrefix()+ ChatColor.RED+"Invalid args");
            player.sendMessage(plugin.getPrefix()+ChatColor.GREEN+"Command Syntax: "+ChatColor.YELLOW+"/lootin rwgchest [CHEST|BARREL|MINECART] [LootTable Name] (Amount)");
            return;
        }
        int amount = 1;
        if(args.length > 2){
            amount = Integer.valueOf(args[2]);
        }
        String containerType = args[0];
        String loottable = args[1];
        ItemStack item = null;
        ItemMeta meta = null;
        if(containerType.equalsIgnoreCase("CHEST")){
            item = new ItemStack(Material.CHEST,amount);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN+"RWG LootTable Chest: "+ChatColor.GOLD+loottable);
        }
        if(containerType.equalsIgnoreCase("BARREL")){
            item = new ItemStack(Material.BARREL,amount);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN+"RWG LootTable Barrel: "+ChatColor.GOLD+loottable);
        }
        if(containerType.equalsIgnoreCase("MINECART")){
            item = new ItemStack(Material.CHEST,amount);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN+"RWG LootTable Chest Minecart: "+ChatColor.GOLD+loottable);
            // meta.setLore();
        }
        if(item != null){
            meta.getPersistentDataContainer().set(LConstants.RWG_CONTAINER_KEY, PersistentDataType.STRING, containerType);
            meta.getPersistentDataContainer().set(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING, loottable);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
            player.sendMessage(plugin.getPrefix()+ChatColor.GREEN+"Gave "+player.getName()+" LootTable "+containerType+ChatColor.YELLOW+loottable);
        }
    }

    @Subcommand("rwg inventory|inv")
    @CommandCompletion("CHEST|BARREL|MINECART")
    public void onRwgInventoryCommand(Player player){
        if(!player.hasPermission("lootin.command.rwg")){
            player.sendMessage(plugin.getMessage(LConstants.NO_PERMISSION,null));
            return;
        }
        RWGCompat compat = plugin.rwgCompat;
        if(compat == null){
            player.sendMessage(plugin.getPrefix()+ChatColor.RED+"You need Realistic World Generator plugin installed to use this command");
            return;
        }
        if(compat.isSetupFailed()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "This feature is not available because the addon setup failed");
            return;
        }
        RwgInventory inventory = new RwgInventory(compat.getHeads(), compat.getApi().getChestStorage());
        inventory.populate();
        player.openInventory(inventory.getInventory());
    }
    
}
