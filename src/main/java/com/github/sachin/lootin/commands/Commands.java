package com.github.sachin.lootin.commands;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.integration.rwg.RWGCompat;
import com.github.sachin.lootin.integration.rwg.util.inventory.RwgInventory;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
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

    @Subcommand("rwg loottable")
    public void onRwgLoottableCommand(Player player){
        if(!testRwg(player)) {
            return;
        }
        RWGCompat compat = plugin.rwgCompat;
        RwgInventory inventory = new RwgInventory(compat.getHeads(), compat.getApi().getChestStorage());
        inventory.populate();
        player.openInventory(inventory.getInventory());
    }

    @Subcommand("rwg elytra")
    public void onRwgElytraCommand(Player player) {
        if(!testRwg(player)) {
            return;
        }
        ItemStack itemStack = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "RWG Elytra ItemFrame");
        meta.getPersistentDataContainer().set(LConstants.RWG_IDENTITY_KEY, PersistentDataType.BYTE, (byte) 0);
        itemStack.setItemMeta(meta);
        player.getInventory().addItem(itemStack);
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You received a Lootin Elytra ItemFrame Placeholder for RWG schematics");
    }

    private boolean testRwg(Player player) {
        if(!player.hasPermission("lootin.command.rwg.loottable")){
            player.sendMessage(plugin.getMessage(LConstants.NO_PERMISSION,null));
            return false;
        }
        RWGCompat compat = plugin.rwgCompat;
        if(compat == null){
            player.sendMessage(plugin.getPrefix()+ChatColor.RED+"You need Realistic World Generator plugin installed to use this command");
            return false; 
        }
        if(compat.isSetupFailed()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "This feature is not available because the addon setup failed");
            return false;
        }
        return true;
    }
    
}
