package com.github.sachin.lootin.listeners;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.LConstants;

import com.github.sachin.prilib.Prilib;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class EntityMetaDataPacketListener extends PacketAdapter{

    private static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    public EntityMetaDataPacketListener() {
         super(Lootin.getPlugin(),TYPE);
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        Entity entity = null;
        try {
            entity = packet.getEntityModifier(event).read(0);
        }catch (Exception ignored){}

        if(entity != null && entity.getType()==EntityType.ITEM_FRAME && entity.getPersistentDataContainer().has(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER)){
            Prilib prilib = Lootin.getPlugin().getPrilib();
            NamespacedKey key = Lootin.getKey(player.getUniqueId().toString());
            if(Lootin.getPlugin().isPost1_19()){
               Object newPacket = prilib.getNmsHandler().getElytraUpdatePacket(packet.getHandle(),entity,key);
               if(newPacket != null){
                event.setPacket(PacketContainer.fromPacket(newPacket));
               }
               return;

            }
            List<WrappedWatchableObject> objects = packet.getWatchableCollectionModifier().readSafely(0);
            for(WrappedWatchableObject object : objects){
                if(object.getIndex()==8){
                    if(entity.getPersistentDataContainer().has(key,PersistentDataType.INTEGER)){
                        object.setValue(new ItemStack(Material.AIR));
                    }

                }
            }

            packet.getWatchableCollectionModifier().write(0, objects);
        }

    }

    
}
