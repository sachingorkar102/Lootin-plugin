package com.github.sachin.lootin.utils.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemSerializer {

    private static ItemStack tempItem;

    private static ItemStack getTempItem(){
        if(tempItem == null){
            tempItem = new ItemStack(Material.STICK);
            ItemMeta meta = tempItem.getItemMeta();
            meta.setDisplayName("This is temporary Item");
            tempItem.setItemMeta(meta);
        }
        return tempItem;
    }


    public static String serialize(List<ItemStack> items){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeInt(items.size());
            for (ItemStack itemStack : items) {
                dataOutput.writeObject(itemStack);
            }
            dataOutput.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static List<ItemStack> deserialize(String string){

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(string));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            List<ItemStack> initialList = new ArrayList<>();
            for (int i = 0; i < items.length; i++) {
                ItemStack item = (ItemStack)dataInput.readObject();
                items[i] = item;
                if(item != null){
                    if(item.isSimilar(getTempItem())){
                        initialList.add(new ItemStack(Material.AIR));
                        continue;
                    }
                }
                initialList.add(items[i]);
            }
            dataInput.close();
            return initialList;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
