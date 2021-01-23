package com.skillw.randomitem;

import com.skillw.randomitem.compute.ItemCompute;
import com.skillw.randomitem.utils.ConfigUtils;
import com.skillw.randomitem.utils.RandomItemUtils;
import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.RandomItemUtils.getMessage;
import static com.skillw.randomitem.utils.RandomItemUtils.loadRandom;

/**
 * @author Glom_
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class RandomItem {

    private static final HashMap<String, RandomItem> RPG_ITEM_HASH_MAP = new HashMap<>();
    private final String id;
    private final Material material;
    private final List<String> lores;
    private final HashMap<String, Object> nbtHashMap;
    private final ItemCompute itemCompute;
    private final ConcurrentHashMap<String, List<String>> stringsMap;
    private final ConcurrentHashMap<String, String> numbersMap;
    private final String display;

    public RandomItem(String id, String display, Material material, List<String> lores, HashMap<String, Object> nbtHashMap, ItemCompute itemCompute, ConcurrentHashMap<String, List<String>> stringsMap, ConcurrentHashMap<String, String> numbersMap) {
        this.id = id;
        this.display = display;
        this.material = material;
        this.lores = lores;
        this.nbtHashMap = nbtHashMap;
        this.itemCompute = itemCompute;
        this.stringsMap = stringsMap;
        this.numbersMap = numbersMap;
    }

    public synchronized static HashMap<String, RandomItem> getRPGItemHashMap() {
        return RandomItem.RPG_ITEM_HASH_MAP;
    }

    public static ItemStack getItemStack(String itemID) {
        RandomItem main = RandomItem.getRPGItemHashMap().get(itemID);
        return main.getItemStack();
    }

    public static ItemStack getItemStack(String itemID, Player player) {
        RandomItem main = RandomItem.getRPGItemHashMap().get(itemID);
        return main.getItemStack(player);
    }

    public static void createItemStackConfig(ItemStack itemStack, String itemKey) {
        try {
            ItemMeta itemMeta = itemStack.getItemMeta();
            String name = itemMeta.getDisplayName();
            Material material = itemStack.getType();
            short sh = (short) itemMeta.getCustomModelData();
            List<String> lores = itemMeta.getLore();
            File file = new File(Main.getInstance().getDataFolder() + "/Items", itemKey + ".yml");
            if (!file.createNewFile()) {
                return;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.createSection(itemKey);
            ConfigurationSection itemKeyS = config.getConfigurationSection(itemKey);
            itemKeyS.createSection("material");
            itemKeyS.createSection("data");
            itemKeyS.createSection("display");
            itemKeyS.createSection("lores");
            itemKeyS.createSection("nbt-keys");
            itemKeyS.createSection("randoms");
            itemKeyS.createSection("computes");
            itemKeyS.set("material", material.toString());
            itemKeyS.set("data", sh);
            itemKeyS.set("display", name);
            itemKeyS.set("lores", lores);
            NBTItem nbtItem = new NBTItem(itemStack);
            for (String key : nbtItem.getKeys()) {
                Object object = nbtItem.getObject(key, Object.class);
                itemKeyS.getConfigurationSection("nbt-keys").set(key, object);
            }
            config.save(file);
            Main.getInstance().loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadObjectsFromFiles() {
        List<File> fileList = RandomItemUtils.getSubFilesFromFile(Main.getInstance().getItemsFile());
        for (File file : fileList) {
            if (file == null) {
                continue;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            //Some codes
            if (config.getKeys(false).isEmpty()) {
                continue;
            }
            for (String key : config.getKeys(false)) {
                ConfigurationSection objectSection = config.getConfigurationSection(key);
                Material material = Material.matchMaterial(objectSection.getString("material"));
                String display = objectSection.getString(("display"));
                List<String> lores = objectSection.getStringList(("lores"));
                ConfigurationSection nbtSection = objectSection.getConfigurationSection("nbt-keys");
                HashMap<String, Object> objectHashmap = new HashMap<>();
                if (nbtSection != null && !nbtSection.getKeys(false).isEmpty()) {
                    for (String nbtKey : nbtSection.getKeys(false)) {
                        objectHashmap.put(nbtKey, nbtSection.getObject(nbtKey, Object.class));
                    }
                }
                ItemCompute itemCompute = ItemCompute.loadConfig(objectSection.getConfigurationSection("computes"));
                ConcurrentHashMap<String, List<String>> stringsMap = new ConcurrentHashMap<>();
                ConcurrentHashMap<String, String> numbersMap = new ConcurrentHashMap<>(ConfigUtils.getNumbersMap());
                {
                    ConfigurationSection randomsSection = objectSection.getConfigurationSection("randoms");
                    loadRandom(stringsMap, numbersMap, randomsSection);
                }
                itemCompute.setRandomMap(numbersMap);
                RandomItem main = new RandomItem(key, display, material, lores, objectHashmap, itemCompute, stringsMap, numbersMap);
                RandomItem.getRPGItemHashMap().put(key, main);
            }
        }
    }

    public ItemStack getItemStack() {
        {
            ItemStack itemStack = new ItemStack(this.material, 1);
            ItemMeta im = itemStack.getItemMeta();

            im.setDisplayName(getMessage(this.display));
            List<String> newLores = new ArrayList<>();
            for (String lore : this.lores) {
                newLores.add(getMessage(lore));
            }
            im.setLore(newLores);
            itemStack.setItemMeta(im);
            NBTItem nbtItem = new NBTItem(itemStack);
            for (String key : this.nbtHashMap.keySet()) {
                Object value = this.nbtHashMap.get(key);
                nbtItem.setObject(key, value);
            }

            return nbtItem.getItem();
        }
    }

    public ItemStack getItemStack(Player player) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta im = itemStack.getItemMeta();
        String display = new String(this.display);
        ConcurrentHashMap<String, String> numbersMap = new ConcurrentHashMap<>();
        for (String key : this.numbersMap.keySet()) {
            String value = PlaceholderAPI.setPlaceholders(player, this.numbersMap.get(key));
            int start = (int) (RandomItemUtils.getResult(value.split(",")[0]));
            int bound = (int) (RandomItemUtils.getResult(value.split(",")[1]));
            numbersMap.put(key, String.valueOf(RandomItemUtils.getRandom(start, bound)));
        }
        ItemCompute itemCompute = new ItemCompute(this.itemCompute.getComputeMapClone());
        itemCompute.setRandomMap(numbersMap);
        ConcurrentHashMap<String, String> alreadyStringsMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> computeMap = itemCompute.calculateCompute(player.getUniqueId());
        for (String key : computeMap.keySet()) {
            String value = computeMap.get(key);
            display = display.replace("{" + key + "}", value);
        }
        {
            List<String> replaces = RandomItemUtils.extractMessageByTriangularBrackets(display);
            for (String replace : replaces) {
                for (String key : stringsMap.keySet()) {
                    if (replace.matches(".*" + key + ".*")) {
                        List<String> values = stringsMap.get(key);
                        String value = RandomItemUtils.getRandomStringWithNext(values);
                        display = display.replace("<" + replace + ">", RandomItemUtils.listToString(RandomItemUtils.getStrings(replace, value)));
                        alreadyStringsMap.put(key, value);
                    }
                }
            }

        }
        for (String key : numbersMap.keySet()) {
            String value = numbersMap.get(key);
            display = display.replace("<" + key + ">", value);
        }
        im.setDisplayName(getMessage(PlaceholderAPI.setPlaceholders(player, display)));
        List<String> newLores = new ArrayList<>();
        List<String> loresClone = new ArrayList<>();
        for (String lore : lores) {
            loresClone.add(new String(lore));
        }
        for (String lore : loresClone) {
            for (String key : computeMap.keySet()) {
                String value = computeMap.get(key);
                lore = lore.replace("{" + key + "}", value);
            }
            {
                List<String> replaces = RandomItemUtils.extractMessageByTriangularBrackets(lore);
                for (String replace : replaces) {
                    for (String key : stringsMap.keySet()) {
                        if (replace.matches(".*" + key + ".*")) {
                            List<String> values = stringsMap.get(key);
                            String value = null;
                            if (!alreadyStringsMap.containsKey(key)) {
                                value = RandomItemUtils.getRandomStringWithNext(values);
                            } else {
                                value = alreadyStringsMap.get(key);
                            }
                            lore = lore.replace("<" + replace + ">", RandomItemUtils.listToStringWithNext(RandomItemUtils.getStrings(replace, value)));
                            alreadyStringsMap.put(key, value);
                        }
                    }
                }
            }
            for (String key : numbersMap.keySet()) {
                String value = numbersMap.get(key);
                lore = lore.replace("<" + key + ">", value);
            }
            String newLore = getMessage(PlaceholderAPI.setPlaceholders(player, lore));
            if (newLore.matches(".*<next>.*")) {
                Collections.addAll(newLores, newLore.split("<next>"));
            } else {
                newLores.add(newLore);
            }
        }
        im.setLore(newLores);
        itemStack.setItemMeta(im);
        NBTItem nbtItem = new NBTItem(itemStack);
        for (String key : nbtHashMap.keySet()) {
            Object value = nbtHashMap.get(key);
            nbtItem.setObject(key, value);
        }

        return nbtItem.getItem();
    }

    public String getId() {
        return this.id;
    }

    public String getDisplay() {
        return this.display;
    }

    public Material getMaterial() {
        return this.material;
    }

    public List<String> getLores() {
        return this.lores;
    }

    public HashMap<String, Object> getNBTHashMap() {
        return this.nbtHashMap;
    }

    public ItemCompute getItemCompute() {
        return itemCompute;
    }
}
