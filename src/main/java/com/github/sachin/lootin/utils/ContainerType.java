package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public enum ContainerType {
    

    CHEST(27,LConstants.TITLE_CHEST) ,DOUBLE_CHEST(54,LConstants.TITLE_DOUBLE_CHEST),MINECART(27,LConstants.TITLE_MINECART),BARREL(27,LConstants.TITLE_BARREL);

    private ContainerType(int slots,String title){
        this.slots = slots;
        this.title = title;
    }

    private int slots;
    private String title;

    public String getTitle(Player player) {
        Lootin plugin = Lootin.getPlugin();
        String t = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(title));

        if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null){
            return PlaceholderAPI.setPlaceholders(player, t);
        }
        return t;
    }

    public int getSlots() {
        return slots;
    }
}
