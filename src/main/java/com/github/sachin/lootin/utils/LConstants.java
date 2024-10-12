package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;

import org.bukkit.NamespacedKey;

public class LConstants {

    public static final NamespacedKey STORAGE_DATA_KEY = Lootin.getKey("file-storage-key");
    public static final NamespacedKey LOOTTABLE_KEY = Lootin.getKey("lotttable");
    public static final NamespacedKey IDENTITY_KEY = new NamespacedKey(Lootin.getPlugin(), "Lootin");
    public static final NamespacedKey DATA_KEY = new NamespacedKey(Lootin.getPlugin(),"loot-container");



    public static final NamespacedKey TRANSFORMER_CHEST_KEY = new NamespacedKey(Lootin.getPlugin(), "lootin-chest");
    public static final NamespacedKey TRANSFORMER_ITEMFRAME_KEY = new NamespacedKey(Lootin.getPlugin(), "lootin-itemframe");
    public static final NamespacedKey TRANSFORMER_MINECART_KEY = new NamespacedKey(Lootin.getPlugin(), "lootin-minecart");
    public static final NamespacedKey ITEM_FRAME_ELYTRA_KEY = Lootin.getKey("item-frame-elytra-key");
    public static final NamespacedKey RWG_LOOTTABLE_KEY = Lootin.getKey("rwg-loottable-key");
    public static final NamespacedKey RWG_IDENTITY_KEY = Lootin.getKey("rwg-identity-key");


    public static final NamespacedKey BETTER_STRUC_KEY = Lootin.getKey("better-structures-name-key");
    public static final NamespacedKey CUSTOM_STRUC_KEY = Lootin.getKey("custom-structures-name-key");

    public static final String TITLE_CHEST = "gui-titles.chest";
    public static final String TITLE_DOUBLE_CHEST = "gui-titles.double-chest";
    public static final String TITLE_MINECART = "gui-titles.minecart";
    public static final String TITLE_BARREL = "gui-titles.barrel";

    public static final String DELETE_ITEMS_CONFIG = "delete-items-on-break";
    public static final String PREVENT_EXPLOSIONS = "prevent-explosions";
    public static final String RESET_SEED = "reset-seed-on-fill";
    public static final String USE_NEW_LISTENER = "use-new-structure-generate-listener";
    public static final String PREVENT_ITEM_PLACING = "prevent-placing-items-in-container";





    public static final String DEBUG_MODE = "debug-mode";


//    public static final String BLACK_LIST_
    public static final String BLACK_LIST_STRUCTURES = "black-list-structures";
    public static final String BLACK_LIST_CUSTOM_STRUCTURES = "black-list-customstructures";
    public static final String BLACK_LIST_OTD_STRUCTURES = "black-list-otd-structures";
    public static final String PER_PLAYER_ELYTRA_ITEM_FRAME = "per-player-elytra-item-frame";
    public static final String PREVENT_ITEM_FILLING_ENABLED = "prevent-filling-containers.enabled";
    public static final String PREVENT_ITEM_FILLING_MSG = "prevent-filling-containers.send-warning-message";
    public static final String BYPASS_GREIF_PLUGINS = "bypass-grief-plugins";

    public static final String KEEP_IN_MEMORY = "keep-in-memory";

    public static final String BLOCK_BREAK_WITHP = "messages.chestbreak-with-permission";
    public static final String BLOCK_BREAK_WITHOUTP = "messages.chestbreak-without-permission";
    public static final String CHEST_EDITED = "messages.chest-edited";
    public static final String NO_PERMISSION = "messages.no-permission";
    public static final String RELOADED = "messages.reloaded";
    public static final String CANT_PLACE_DCHEST = "messages.cant-place-double-chest";
    public static final String ELYTRA_IF_BREAK_WITHPERM = "messages.elytra-itemframe-break-with-permission";
    public static final String ELYTRA_IF_BREAK_WITHOUTPERM = "messages.elytra-itemframe-break-without-permission";
    public static final String ELYTRA_IF_REMOVED = "messages.elytra-item-frame-removed";
    public static final String CANT_PLACE_ITEMS = "messages.cant-put-items-in-loot-container";
    public static final String LOOK_AT_CONTAINER = "messages.look-at-container";


    public static final String SERVER_VERSION = "";
    public static final int SERVER_MINOR_VERSION = 0;

    public static final boolean SERVER_REMAPPED = LConstants.SERVER_MINOR_VERSION >= 17;

    public static final String WG_FLAG_NAME = "lootin-container-access";

}
