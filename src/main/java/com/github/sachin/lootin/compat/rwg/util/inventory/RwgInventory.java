package com.github.sachin.lootin.compat.rwg.util.inventory;

import com.github.sachin.lootin.utils.LConstants;
import com.syntaxphoenix.syntaxapi.utils.java.UniCode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.sourcewriters.spigot.rwg.legacy.api.chest.IChestStorage;

public class RwgInventory implements InventoryHolder {

    private final IChestStorage storage;
    private final Inventory inventory;
    private final ItemStack[] heads;
    private int page = 0;

    private boolean type = false;
    private String selected = null;

    private boolean hasNext = false;
    private boolean hasPrevious = false;

    public RwgInventory(ItemStack[] heads, IChestStorage storage) {
        this.inventory = Bukkit.createInventory(this, 45, ChatColor.GOLD + "RWG Loottables");
        this.storage = storage;
        this.heads = heads;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void setupItemMenu(ItemStack[] heads) {
        ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("");
        stack.setItemMeta(meta);
        for(int index = 27; index < 45; index++) {
            inventory.setItem(index, stack);
        }
        if(page != 0) {
            stack = heads[0];
            meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "" + UniCode.ARROWS_LEFT + " Previous Page");
            stack.setItemMeta(meta);
            stack.setAmount(page);
            inventory.setItem(37, stack);
            hasPrevious = true;
        }
        stack = heads[1];
        meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Current Page");
        stack.setItemMeta(meta);
        stack.setAmount(page + 1);
        inventory.setItem(40, stack);
        if(page + 1 != getMaxPage()) {
            stack = heads[2];
            meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Next Page " + UniCode.ARROWS_RIGHT);
            stack.setItemMeta(meta);
            stack.setAmount(page + 2);
            inventory.setItem(43, stack);
        }
        String[] names = storage.getNames();
        int startIndex = page * 27;
        int planIndex = startIndex + 26;
        int endIndex = planIndex >= names.length ? names.length - 1 : planIndex;
        int index = 0;
        for(int i = startIndex; i <= endIndex; i++) {
            String loottable = names[i];
            stack = new ItemStack(Material.PAPER);
            meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + loottable);
            stack.setItemMeta(meta);
            inventory.setItem(index++, stack);
            hasNext = true;
        }
        while(index < 27) {
            inventory.setItem(index++, null);
        }
    }

    private void setupTypeMenu() {
        ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("");
        stack.setItemMeta(meta);
        for(int index = 0; index < 45; index++) {
            inventory.setItem(index, stack);
        }
        stack = new ItemStack(Material.PAPER);
        meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + selected);
        stack.setItemMeta(meta);
        inventory.setItem(4, stack);
        stack = new ItemStack(Material.BARREL);
        meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Barrel");
        stack.setItemMeta(meta);
        inventory.setItem(19, stack);
        stack = new ItemStack(Material.MINECART);
        meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Minecart");
        stack.setItemMeta(meta);
        inventory.setItem(22, stack);
        stack = new ItemStack(Material.CHEST);
        meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Chest");
        stack.setItemMeta(meta);
        inventory.setItem(25, stack);
        stack = new ItemStack(Material.BARRIER);
        meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Back");
        stack.setItemMeta(meta);
        inventory.setItem(40, stack);
    }

    public void populate() {
        if(type) {
            setupTypeMenu();
            return;
        }
        setupItemMenu(heads);
    }

    public boolean isType(){
        return type;
    }

    public boolean hasNext(){
        return hasNext;
    }

    public boolean hasPrevious(){
        return hasPrevious;
    }

    public void giveItem(Inventory inventory, int slot){
        String name;
        Material material;
        switch(slot){
            case 19:
                name = "Barrel: ";
                material = Material.BARREL;
                break;
            case 22:
                name = "Minecart: ";
                material = Material.FURNACE;
                break;
            case 25:
                name = "Chest: ";
                material = Material.CHEST;
                break;
            default:
                return;
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "RWG " + name + ChatColor.YELLOW + selected);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(LConstants.RWG_IDENTITY_KEY, PersistentDataType.BYTE, (byte) 0);
        container.set(LConstants.RWG_LOOTTABLE_KEY, PersistentDataType.STRING, selected);
        stack.setItemMeta(meta);
        inventory.addItem(stack);
    }

    public void selectItem(int slot) {
        if(slot == -1) {
            this.type = false;
            return;
        }
        String[] names = storage.getNames();
        if(slot >= names.length) {
            return;
        }
        this.selected = storage.getNames()[slot + (page * 27)];
        this.type = true;
    }

    public void select(int page) {
        int maxPage = getMaxPage();
        if(page >= maxPage) {
            this.page = maxPage - 1;
            return;
        }
        if(page < 0) {
            this.page = 0;
            return;
        }
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getMaxPage(){
        return (int) Math.ceil(storage.getNames().length / 27d);
    }
    
}
