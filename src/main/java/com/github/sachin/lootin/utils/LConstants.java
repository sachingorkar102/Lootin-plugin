package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;

import org.bukkit.NamespacedKey;

public class LConstants {

    public static final String IDENTITY_KEY_STRING = "Lootin";
    public static final String DATA_KEY_STRING = "loot-container";
    public static final NamespacedKey IDENTITY_KEY = new NamespacedKey(Lootin.getPlugin(), IDENTITY_KEY_STRING);
    public static final NamespacedKey DATA_KEY = new NamespacedKey(Lootin.getPlugin(), DATA_KEY_STRING);
    public static final NamespacedKey ITEM_FRAME_ELYTRA_KEY = Lootin.getKey("item-frame-elytra-key");
    public static final NamespacedKey RWG_LOOTTABLE_KEY = Lootin.getKey("rwg-loottable-key");
    public static final NamespacedKey RWG_IDENTITY_KEY = Lootin.getKey("rwg-identity-key");

    public static final String TITLE_CHEST = "gui-titles.chest";
    public static final String TITLE_DOUBLE_CHEST = "gui-titles.double-chest";
    public static final String TITLE_MINECART = "gui-titles.minecart";
    public static final String TITLE_BARREL = "gui-titles.barrel";

    public static final String DELETE_ITEMS_CONFIG = "delete-items-on-break";
    public static final String PREVENT_EXPLOSIONS = "prevent-explosions";
    public static final String BLACK_LIST_STRUCTURES = "black-list-structures";
    public static final String BLACK_LIST_CUSTOM_STRUCTURES = "black-list-customstructures";
    public static final String BLACK_LIST_OTD_STRUCTURES = "black-list-otd-structures";
    public static final String PER_PLAYER_ELYTRA_ITEM_FRAME = "per-player-elytra-item-frame";
    public static final String BYPASS_GREIF_PLUGINS = "bypass-grief-plugins";

    public static final String BLOCK_BREAK_WITHP = "messages.chestbreak-with-permission";
    public static final String BLOCK_BREAK_WITHOUTP = "messages.chestbreak-without-permission";
    public static final String CHEST_EDITED = "messages.chest-edited";
    public static final String NO_PERMISSION = "messages.no-permission";
    public static final String RELOADED = "messages.reloaded";
    public static final String CANT_PLACE_DCHEST = "messages.cant-place-double-chest";
    public static final String ELYTRA_IF_BREAK_WITHPERM = "messages.elytra-itemframe-break-with-permission";
    public static final String ELYTRA_IF_BREAK_WITHOUTPERM = "messages.elytra-itemframe-break-without-permission";
    public static final String ELYTRA_IF_REMOVED = "messages.elytra-item-frame-removed";
    
}
