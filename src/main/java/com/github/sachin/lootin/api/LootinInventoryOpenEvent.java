package com.github.sachin.lootin.api;

import com.github.sachin.lootin.utils.storage.LootinContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LootinInventoryOpenEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private List<ItemStack> items;

    private Lootable lootable;
    private boolean cancelled;
    private boolean isRefill;

    public LootinInventoryOpenEvent(Player player, Lootable lootable, @Nullable List<ItemStack> items, boolean isRefill){
        this.player = player;
        this.lootable = lootable;
        this.items = items;
        this.cancelled = false;
        this.isRefill = isRefill;
    }

    public Lootable getLootable() {
        return lootable;
    }

    public Player getPlayer() {
        return player;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public boolean isRefill() {
        return isRefill;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
