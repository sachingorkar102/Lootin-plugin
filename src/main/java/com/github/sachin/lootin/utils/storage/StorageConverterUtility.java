package com.github.sachin.lootin.utils.storage;

import com.github.sachin.lootin.Lootin;
import com.github.sachin.lootin.utils.ChestUtils;
import com.github.sachin.lootin.utils.LConstants;
import com.github.sachin.lootin.utils.storage.ItemSerializer;
import com.github.sachin.lootin.utils.storage.LootinContainer;
import com.github.sachin.lootin.utils.storage.PlayerLootData;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

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
                if(holder.getPersistentDataContainer().has(key, PersistentDataType.STRING)){
                    List<ItemStack> oldItems = ItemSerializer.deserialize(holder.getPersistentDataContainer().get(key,PersistentDataType.STRING));
                    ChestUtils.updatePersistentStorageTypes(holder.getPersistentDataContainer(),((Container)holder).getInventory(),oldItems,key);
                }
                if(holder.getPersistentDataContainer().has(key,DataType.ITEM_STACK_ARRAY)){
                    ItemStack[] items = holder.getPersistentDataContainer().get(key, DataType.ITEM_STACK_ARRAY);
                    UUID uuid = UUID.fromString(key.getKey());
                    PlayerLootData playerData = new PlayerLootData(uuid,Arrays.asList(items),System.currentTimeMillis(),0);
                    lootinContainer.getPlayerDataMap().put(uuid,playerData);
                    holder.getPersistentDataContainer().remove(key);
                }
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
        UUID playerID = UUID.fromString(key);
        LootinContainer lootinContainer = getContainerData(containerID);
        PlayerLootData playerData = lootinContainer.getPlayerDataMap().get(playerID);
        if(playerData != null){
            playerData.setItems(items);
        }else{
            playerData = new PlayerLootData(playerID,items,System.currentTimeMillis(),0);
        }
        lootinContainer.getPlayerDataMap().put(playerID,playerData);
        lootinContainer.resetClosingTimer();
        plugin.cachedContainers.put(containerID,lootinContainer);
    }

    public static void save(LootinContainer lootinContainer){
        YamlConfiguration yaml = new YamlConfiguration();
        for(UUID key : lootinContainer.getPlayerDataMap().keySet()){
            PlayerLootData playerData = lootinContainer.getPlayerDataMap().get(key);
            yaml.set(key.toString()+".items",playerData.getItems());
            yaml.set(key.toString()+".last-loot-time",playerData.getLastLootTime());
            yaml.set(key.toString()+".refills",playerData.getRefills());
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
                UUID uuid = UUID.fromString(key);
                PlayerLootData playerData = new PlayerLootData(uuid);
                if(yaml.isList(key)){
                    playerData.setItems((List<ItemStack>) yaml.getList(key));
                    playerData.setLastLootTime(System.currentTimeMillis());
                    playerData.setRefills(0);
                }
                else if(yaml.isConfigurationSection(key)){
                    ConfigurationSection subConfig = yaml.getConfigurationSection(key);
                    playerData.setItems((List<ItemStack>) subConfig.getList("items"));
                    playerData.setLastLootTime(subConfig.getLong("last-loot-time"));
                    playerData.setRefills(subConfig.getInt("refills"));
                }
                container.getPlayerDataMap().put(uuid,playerData);
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
