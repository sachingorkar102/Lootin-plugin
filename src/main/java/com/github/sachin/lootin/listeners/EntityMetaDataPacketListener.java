package com.github.sachin.lootin.listeners;

import java.util.Arrays;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.LConstants;

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
        // super(Lootin.getPlugin(),TYPE);
        super(Lootin.getPlugin(),ListenerPriority.NORMAL,Arrays.asList(TYPE),ListenerOptions.ASYNC);
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        if(packet.getType() != TYPE) {
            return;
        }
        Entity entity = packet.getEntityModifier(event).read(0);
        if(entity != null && entity.getType()==EntityType.ITEM_FRAME && entity.getPersistentDataContainer().has(LConstants.ITEM_FRAME_ELYTRA_KEY, PersistentDataType.INTEGER)){
            NamespacedKey key = Lootin.getKey(player.getUniqueId().toString());
            List<WrappedWatchableObject> objects = packet.getWatchableCollectionModifier().read(0);
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
