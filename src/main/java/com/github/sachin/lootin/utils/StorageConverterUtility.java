package com.github.sachin.lootin.utils;

import com.github.sachin.lootin.Lootin;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StorageConverterUtility {

    private static final Lootin plugin = Lootin.getPlugin();


    public static LootinContainer convert(PersistentDataHolder holder){
        UUID containerID = UUID.randomUUID();
        LootinContainer lootinContainer = new LootinContainer(containerID);

        for(NamespacedKey key : holder.getPersistentDataContainer().getKeys()){
            if(key.getNamespace().equals("lootin") && isValidUUID(key.getKey())){
                ItemStack[] items = holder.getPersistentDataContainer().get(key, DataType.ITEM_STACK_ARRAY);
                lootinContainer.getItemMap().put(UUID.fromString(key.getKey()),Arrays.asList(items));
                holder.getPersistentDataContainer().remove(key);
            }
        }
        holder.getPersistentDataContainer().set(LConstants.STORAGE_DATA_KEY,DataType.UUID,containerID);
        if(holder instanceof Container){
            ((Container)holder).update();
        }
        plugin.cachedContainers.put(containerID,lootinContainer);
        return lootinContainer;
    }



    public static void update(PersistentDataHolder holder, String key,List<ItemStack> items){
        UUID containerID = holder.getPersistentDataContainer().get(LConstants.STORAGE_DATA_KEY,DataType.UUID);

        LootinContainer lootinContainer = getContainerData(containerID);
        lootinContainer.getItemMap().put(UUID.fromString(key),items);
        lootinContainer.resetClosingTimer();
        plugin.cachedContainers.put(containerID,lootinContainer);
    }

    public static void save(LootinContainer lootinContainer){
        YamlConfiguration yaml = new YamlConfiguration();
        for(UUID key : lootinContainer.getItemMap().keySet()){
            yaml.set(key.toString(),lootinContainer.getItemMap().get(key));
        }
        saveToFile(yaml,lootinContainer.getContainerID());

    }

    public static LootinContainer getContainerData(UUID containerID){
        LootinContainer container = new LootinContainer(containerID);
        if(plugin.cachedContainers.containsKey(containerID)){
            return plugin.cachedContainers.get(containerID);
        }
        File file = new File(getDataFile(),containerID.toString());
        if(!file.exists()) return container;
        try {
            byte[] compressedData = readFromFile(file);
            byte[] decompressedData = decompress(compressedData);
            YamlConfiguration yaml = deserizeData(decompressedData);
            for(String key : yaml.getKeys(false)){
                container.getItemMap().put(UUID.fromString(key),(List<ItemStack>) yaml.getList(key));
            }
            plugin.cachedContainers.put(containerID,container);
//            file.delete();
            return container;
        } catch (IOException e) {
            e.printStackTrace();
            return container;
        }
    }


    private static byte[] serializeData(YamlConfiguration yaml) throws IOException {
        return yaml.saveToString().getBytes(StandardCharsets.UTF_8);
    }

    private static YamlConfiguration deserizeData(byte[] data){
        String yamlString = new String(data,StandardCharsets.UTF_8);
        return YamlConfiguration.loadConfiguration(new StringReader(yamlString));
    }

    private static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(data);
        }
        return byteStream.toByteArray();
    }

    private static byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipStream = new GZIPInputStream(byteStream)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, len);
            }
        }
        return outStream.toByteArray();
    }

    private static void saveToFile(YamlConfiguration yaml,UUID containerID){
        try {
            byte[] serializedData = serializeData(yaml);
            byte[] compressedData = compress(serializedData);
            writeToFile(compressedData,containerID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeToFile(byte[] data,UUID containerID) throws IOException {
        File file = new File(getDataFile(),containerID.toString());
        if(!file.exists()) file.createNewFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(data);
        }
    }

    private static byte[] readFromFile(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] data = new byte[fileInputStream.available()];
            fileInputStream.read(data);
            return data;
        }
    }

    public static boolean isValidUUID(String string){
        try {
            UUID uuid = UUID.fromString(string);
            return uuid.toString().equals(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static File getDataFile(){
        File file = new File(Lootin.getPlugin().getDataFolder(),"data");
        if(!file.exists()){
            file.mkdir();
        }
        return file;
    }
}
