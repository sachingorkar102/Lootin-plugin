package com.github.sachin.lootin.commands;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.compat.rwg.RWGCompat;
import com.github.sachin.lootin.compat.rwg.util.inventory.RwgInventory;
import com.github.sachin.lootin.utils.*;

import com.github.sachin.lootin.utils.storage.LootinContainer;
import com.github.sachin.lootin.utils.storage.PlayerLootData;
import com.github.sachin.lootin.utils.storage.StorageConverterUtility;
import com.jeff_media.morepersistentdatatypes.DataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
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
    public void onSet(Player player, String[] args) {
        if (args.length < 1) return;

        if (!player.hasPermission("lootin.command.set")) {
            plugin.sendPlayerMessage(LConstants.NO_PERMISSION, player);
            return;
        }

        String type = args[0];
        RayTraceResult ray = player.rayTraceBlocks(4);

        switch (type.toUpperCase()) {
            case "CHEST":
            case "TRAPPED_CHEST":
                handleBlockContainer(player, ray, Material.CHEST, Material.TRAPPED_CHEST, ContainerType.CHEST);
                break;
            case "BARREL":
                handleBlockContainer(player, ray, Material.BARREL, null, ContainerType.BARREL);
                break;
            case "MINECART":
                handleMinecartContainer(player);
                break;
            default:
                plugin.sendPlayerMessage("&cInvalid container type!", player);
                break;
        }
    }

    private void handleBlockContainer(Player player, RayTraceResult ray, Material mainType, Material altType, ContainerType containerType) {
        if (ray != null && (ray.getHitBlock().getType() == mainType || (altType != null && ray.getHitBlock().getType() == altType))) {
            BlockState blockState = ray.getHitBlock().getState();
            Inventory inventory = (blockState instanceof Chest) ? ((Chest) blockState).getInventory() : ((Barrel) blockState).getInventory();

            if (!inventory.isEmpty()) {
                ChestUtils.setLootinContainer(null, blockState, containerType);
                ((PersistentDataHolder)blockState).getPersistentDataContainer().set(LConstants.CUSTOM_CONTAINER_KEY,PersistentDataType.INTEGER,1);
                blockState.update();
                plugin.sendPlayerMessage("&a" + containerType.name() + " set as lootin container successfully, the contents are per player now!", player);
            } else {
                plugin.sendPlayerMessage("&c" + containerType.name() + " is empty!", player);
            }
        } else {
            plugin.sendPlayerMessage(LConstants.LOOK_AT_CONTAINER, player);
        }
    }

    private void handleMinecartContainer(Player player) {
        RayTraceResult raytrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 4, (en) -> en.getType() == EntityType.MINECART_CHEST);
        if (raytrace != null && raytrace.getHitEntity() != null) {
            StorageMinecart minecart = (StorageMinecart) raytrace.getHitEntity();
            if (!minecart.getInventory().isEmpty()) {
                ChestUtils.setLootinContainer(minecart, null, ContainerType.MINECART);
                minecart.getPersistentDataContainer().set(LConstants.CUSTOM_CONTAINER_KEY,PersistentDataType.INTEGER,1);
                plugin.sendPlayerMessage("&aMinecart set as lootin container successfully, the contents are per player now!", player);
            } else {
                plugin.sendPlayerMessage("&cMinecart is empty!", player);
            }
        } else {
            plugin.sendPlayerMessage(LConstants.LOOK_AT_CONTAINER, player);
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
                if(!lootinContainer.getPlayerDataMap().isEmpty()){
                    plugin.sendPlayerMessage("&aCleared data of &e"+lootinContainer.getPlayerDataMap() .size()+"&a players from the container",player);
                    lootinContainer.getPlayerDataMap().clear();
                    plugin.cachedContainers.put(lootinContainer.getContainerID(),lootinContainer);
                }
                else{
                    plugin.sendPlayerMessage("&cNo data found of any player in the container.",player);
                }
            }
            else if(args[0].equalsIgnoreCase("player") && args.length>=2){
                Iterator<Map.Entry<UUID, PlayerLootData>> iterator = lootinContainer.getPlayerDataMap().entrySet().iterator();
                while (iterator.hasNext() ){
                    Map.Entry<UUID, PlayerLootData> entry = iterator.next();
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
            lootinContainer.getPlayerDataMap().keySet().forEach(i -> playerNames.add(Bukkit.getOfflinePlayer(i).getName()));
            TextComponent baseMsg = Component.text("Players: [",NamedTextColor.GOLD);
            TextComponent separator = Component.text(", ",NamedTextColor.WHITE);
            int maxRefills = plugin.getWorldManager().getMaxRefills(player.getWorld().getName());
            for (PlayerLootData playerLootData : lootinContainer.getPlayerDataMap().values()) {
                double timeLeftMillis = ((double) ((playerLootData.getLastLootTime() + plugin.getWorldManager().getRefillTime(player.getWorld().getName())) - System.currentTimeMillis()));
                if (timeLeftMillis < 0) timeLeftMillis = 0;
                String timeLeftString;
                if (timeLeftMillis >= 86400000) {
                    double timeLeftD = timeLeftMillis / 86400000;
                    timeLeftString = String.format("%.2f", timeLeftD) + " Days";
                }
                else if (timeLeftMillis >= 3600000) {
                    double timeLeftD = timeLeftMillis / 3600000;
                    timeLeftString = String.format("%.2f", timeLeftD) + " Hours";
                }
                else if (timeLeftMillis >= 60000) {
                    double timeLeftD = timeLeftMillis / 60000;
                    timeLeftString = String.format("%.2f", timeLeftD) + " Minutes";
                }
                else {
                    double timeLeftD = timeLeftMillis / 1000;
                    timeLeftString = String.format("%.2f", timeLeftD) + " Seconds";
                }
                TextComponent refillComponent;
                TextComponent timeLeftComponent = Component.text("TimeLeft: " + timeLeftString,NamedTextColor.GREEN);
                if(maxRefills!=-1 && playerLootData.getRefills()>=maxRefills){
                    refillComponent = Component.text("Refills: "+playerLootData.getRefills(),NamedTextColor.RED);
                }else{
                    refillComponent = Component.text("Refills: "+playerLootData.getRefills(),NamedTextColor.GREEN);
                }
                TextComponent playerComponent = Component.text(Bukkit.getOfflinePlayer(playerLootData.getPlayerID()).getName(), NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(refillComponent.append(Component.text("\n").append(timeLeftComponent))));
                baseMsg = baseMsg.append(playerComponent);
                if(lootinContainer.getPlayerDataMap().size()>1){
                    baseMsg = baseMsg.append(separator);
                }
            }
            baseMsg = baseMsg.append(Component.text("]",NamedTextColor.GOLD));
            plugin.sendPlayerMessage("&aContainerID: &e"+lootinContainer.getContainerID(),player);
            player.sendMessage(baseMsg);
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
