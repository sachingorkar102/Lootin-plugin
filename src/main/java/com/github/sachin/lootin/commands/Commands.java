package com.github.sachin.lootin.commands;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.compat.rwg.RWGCompat;
import com.github.sachin.lootin.compat.rwg.util.inventory.RwgInventory;
import com.github.sachin.lootin.utils.*;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;

import java.util.*;

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
            plugin.sendPlayerMessage(LConstants.NO_PERMISSION,player);
            return;
        }
        String type = args[0];
        RayTraceResult ray = player.rayTraceBlocks(4);
        if(type.equals("CHEST")){
            if(ray != null && (ray.getHitBlock().getType()==Material.CHEST || ray.getHitBlock().getType()==Material.TRAPPED_CHEST)){
                Chest chest = (Chest) ray.getHitBlock().getState();
                if(!chest.getInventory().isEmpty()){
                    ChestUtils.setLootinContainer(null, chest, ContainerType.CHEST);
                    plugin.sendPlayerMessage("&aChest set as lootin container successfully, the contents of chest are per player now!", player);
                }
                else{
                    plugin.sendPlayerMessage("&cChest is empty!", player);
                }
                
            }
            else{
                plugin.sendPlayerMessage(LConstants.LOOK_AT_CONTAINER, player);
            }
        }
        else if(type.equals("BARREL")){
            if(ray != null && ray.getHitBlock().getType()==Material.BARREL){
                Barrel barrel = (Barrel) ray.getHitBlock().getState();
                if(!barrel.getInventory().isEmpty()){
                    ChestUtils.setLootinContainer(null, barrel, ContainerType.BARREL);
                    plugin.sendPlayerMessage("&aBarrel set as lootin container successfully, the contents of barrel are per player now!", player);
                }
                else{
                    plugin.sendPlayerMessage("&cBarrel is empty!", player);
                }
            }
            else{
                plugin.sendPlayerMessage(LConstants.LOOK_AT_CONTAINER, player);
            }
        }
        else if(type.equals("MINECART")){
            RayTraceResult raytrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 4,(en) -> en.getType()==EntityType.MINECART_CHEST);
            if(raytrace != null && raytrace.getHitEntity() != null){
                StorageMinecart minecart = (StorageMinecart) raytrace.getHitEntity();
                if(!minecart.getInventory().isEmpty()){
                    ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);
                    plugin.sendPlayerMessage("&aChest Minecart set as lootin container successfully, the contents of minecart are per player now!", player);
                }
                else{
                    plugin.sendPlayerMessage("&cMinecart is empty!", player);
                }
            }
            else{
                plugin.sendPlayerMessage(LConstants.LOOK_AT_CONTAINER, player);
            }
        }
    }

    @Subcommand("clear")
    @CommandCompletion("all|player @players @nothing")
    public void onClearCommand(Player player,String[] args){
        if(!player.hasPermission("lootin.command.clear")){
            plugin.sendPlayerMessage(LConstants.NO_PERMISSION,player);
            return;
        }
        if(args.length<1) return;
        LootinContainer lootinContainer = getTargetContainer(player);
        if(lootinContainer != null){
            if(args[0].equalsIgnoreCase("all")){
                if(!lootinContainer.getItemMap().isEmpty()){
                    plugin.sendPlayerMessage("&aCleared data of &e"+lootinContainer.getItemMap().size()+"&a players from the container",player);
                    lootinContainer.getItemMap().clear();
                    plugin.cachedContainers.put(lootinContainer.getContainerID(),lootinContainer);
                }
                else{
                    plugin.sendPlayerMessage("&cNo data found of any player in the container.",player);
                }
            }
            else if(args[0].equalsIgnoreCase("player") && args.length>=2){
                Iterator<Map.Entry<UUID, List<ItemStack>>> iterator = lootinContainer.getItemMap().entrySet().iterator();
                while (iterator.hasNext() ){
                    Map.Entry<UUID, List<ItemStack>> entry = iterator.next();
                    if(args[1].equals(Bukkit.getOfflinePlayer(entry.getKey()).getName())){
                        iterator.remove();
                        plugin.sendPlayerMessage("&aCleared data of &e"+args[1]+"&a from the container.",player);
                        plugin.cachedContainers.put(lootinContainer.getContainerID(),lootinContainer);
                        return;
                    }
                }
                plugin.sendPlayerMessage("&cNo data found for the player &6"+args[1]+" &cin the container.",player);
            }
        }else{
            plugin.sendPlayerMessage(LConstants.LOOK_AT_CONTAINER, player);
        }
    }

    @Subcommand("info")
    public void onInfoCommand(Player player){
        if(!player.hasPermission("lootin.command.info")){
            plugin.sendPlayerMessage(LConstants.NO_PERMISSION,player);
            return;
        }
        LootinContainer lootinContainer = getTargetContainer(player);

        if(lootinContainer != null){
            List<String> playerNames = new ArrayList<>();
            lootinContainer.getItemMap().keySet().forEach(i -> playerNames.add(Bukkit.getOfflinePlayer(i).getName()));
            plugin.sendPlayerMessage("&aContainerID: &e"+lootinContainer.getContainerID(),player);
            plugin.sendPlayerMessage("&aPlayers: &e"+playerNames,player);
        }
        else{
            plugin.sendPlayerMessage(LConstants.LOOK_AT_CONTAINER, player);
        }

    }

    private LootinContainer getTargetContainer(Player player){
        LootinContainer lootinContainer = null;
        PersistentDataHolder holder = null;
        RayTraceResult blockRay = player.rayTraceBlocks(4);
        RayTraceResult entiryRay = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 4,(en) -> en.getType()==EntityType.MINECART_CHEST);
        if(blockRay != null && blockRay.getHitBlock().getState() instanceof PersistentDataHolder){
            holder = (PersistentDataHolder) blockRay.getHitBlock().getState();

        }
        else if(entiryRay != null && entiryRay.getHitEntity() != null){
            holder = entiryRay.getHitEntity();
        }

        if(holder == null) return null;
        if(holder.getPersistentDataContainer().has(LConstants.STORAGE_DATA_KEY)){
            lootinContainer = StorageConverterUtility.getContainerData(holder.getPersistentDataContainer().get(LConstants.STORAGE_DATA_KEY,DataType.UUID));
        }
        else if(holder.getPersistentDataContainer().has(LConstants.DATA_KEY)){
            lootinContainer = StorageConverterUtility.convert(holder);
        }

        return lootinContainer;
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
            plugin.sendPlayerMessage(LConstants.NO_PERMISSION,player);
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
