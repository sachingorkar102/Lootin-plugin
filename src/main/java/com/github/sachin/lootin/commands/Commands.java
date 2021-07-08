package com.github.sachin.lootin.commands;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.LConstants;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
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
    
}
