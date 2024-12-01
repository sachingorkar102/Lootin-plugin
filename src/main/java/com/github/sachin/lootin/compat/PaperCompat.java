package com.github.sachin.lootin.compat;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.storage.LootinContainer;
import com.github.sachin.lootin.utils.storage.PlayerLootData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PaperCompat {

    public static void sendPlayerMessage(LootinContainer lootinContainer, Player player){
        List<String> playerNames = new ArrayList<>();
        lootinContainer.getPlayerDataMap().keySet().forEach(i -> playerNames.add(Bukkit.getOfflinePlayer(i).getName()));
        TextComponent baseMsg = Component.text("Players: [",NamedTextColor.GOLD);
        TextComponent separator = Component.text(", ",NamedTextColor.WHITE);
        int maxRefills = Lootin.getPlugin().getWorldManager().getMaxRefills(player.getWorld().getName());
        for (PlayerLootData playerLootData : lootinContainer.getPlayerDataMap().values()) {
            double timeLeftMillis = ((double) ((playerLootData.getLastLootTime() + Lootin.getPlugin().getWorldManager().getRefillTime(player.getWorld().getName())) - System.currentTimeMillis()));
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
        Lootin.getPlugin().sendPlayerMessage("&aContainerID: &e"+lootinContainer.getContainerID(),player);
        player.sendMessage(baseMsg);
    }
}
