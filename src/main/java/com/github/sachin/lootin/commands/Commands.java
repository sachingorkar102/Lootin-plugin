package com.github.sachin.lootin.commands;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.compat.rwg.RWGCompat;
import com.github.sachin.lootin.compat.rwg.util.inventory.RwgInventory;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.ContainerType;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

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

    @Subcommand("set")
    @CommandCompletion("CHEST|BARREL|MINECART")
    public void onSet(Player player,String[] args){
        if(args.length<1) return;
        if(!player.hasPermission("lootin.command.set")){
            player.sendMessage(plugin.getMessage(LConstants.NO_PERMISSION,null));
            return;
        }
        String type = args[0];
        RayTraceResult ray = player.rayTraceBlocks(4);
        if(type.equals("CHEST")){
            if(ray != null && (ray.getHitBlock().getType()==Material.CHEST || ray.getHitBlock().getType()==Material.TRAPPED_CHEST)){
                Chest chest = (Chest) ray.getHitBlock().getState();
                if(!chest.getInventory().isEmpty()){
                    ChestUtils.setLootinContainer(null, chest, ContainerType.CHEST);
                    player.sendMessage(plugin.getMessage("&aChest set as lootin container successfully, the contents of chest are per player now!", player));
                }
                else{
                    player.sendMessage(plugin.getMessage("&cChest is empty!", player));
                }
                
            }
            else{
                player.sendMessage(plugin.getMessage("&cLook at chest while executing command!", player));
            }
        }
        else if(type.equals("BARREL")){
            if(ray != null && ray.getHitBlock().getType()==Material.BARREL){
                Barrel barrel = (Barrel) ray.getHitBlock().getState();
                if(!barrel.getInventory().isEmpty()){
                    ChestUtils.setLootinContainer(null, barrel, ContainerType.BARREL);
                    player.sendMessage(plugin.getMessage("&aBarrel set as lootin container successfully, the contents of barrel are per player now!", player));
                }
                else{
                    player.sendMessage(plugin.getMessage("&cBarrel is empty!", player));
                }
                
            }
            else{
                player.sendMessage(plugin.getMessage("&cLook at barrel while executing command!", player));
            }
        }
        else if(type.equals("MINECART")){
            RayTraceResult raytrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 4,(en) -> en.getType()==EntityType.MINECART_CHEST);
            if(raytrace != null && raytrace.getHitEntity() != null){
                StorageMinecart minecart = (StorageMinecart) raytrace.getHitEntity();
                if(!minecart.getInventory().isEmpty()){
                    ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);
                    player.sendMessage(plugin.getMessage("&aChest Minecart set as lootin container successfully, the contents of minecart are per player now!", player));
                }
                else{
                    player.sendMessage(plugin.getMessage("&cMinecart is empty!", player));
                }
            }
            else{
                player.sendMessage(plugin.getMessage("&cLook at Chest Minecart while executing command!", player));
            }
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
