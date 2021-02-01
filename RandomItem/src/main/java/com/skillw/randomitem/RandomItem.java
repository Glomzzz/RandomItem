package com.skillw.randomitem;

import com.skillw.randomitem.compute.ItemCompute;
import com.skillw.randomitem.utils.ConfigUtils;
import com.skillw.randomitem.utils.RandomItemUtils;
import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.RandomItemUtils.*;

/**
 * @author Glom_
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class RandomItem {

    private static final HashMap<String, RandomItem> RPG_ITEM_HASH_MAP = new HashMap<>();
    private final String id;
    private final String material;
    private final List<String> lores;
    private final HashMap<String, String> nbtHashMap;
    private final ItemCompute itemCompute;
    private final ConcurrentHashMap<String, List<String>> stringsMap;
    private final ConcurrentHashMap<String, String> numbersMap;
    private final ConcurrentHashMap<String, String> enchantsMap;
    private final String display;
    private final short data;

    public RandomItem(String id, String display, String material, List<String> lores, HashMap<String, String> nbtHashMap, ItemCompute itemCompute, ConcurrentHashMap<String, List<String>> stringsMap, ConcurrentHashMap<String, String> numbersMap, ConcurrentHashMap<String, String> enchantsMap, short data) {
        this.id = id;
        this.display = display;
        this.material = material;
        this.lores = lores;
        this.nbtHashMap = nbtHashMap;
        this.itemCompute = itemCompute;
        this.stringsMap = stringsMap;
        this.numbersMap = numbersMap;
        this.enchantsMap = enchantsMap;
        this.data = data;
    }

    public synchronized static HashMap<String, RandomItem> getRPGItemHashMap() {
        return RandomItem.RPG_ITEM_HASH_MAP;
    }

    public static ItemStack getItemStack(String itemID, Player player) {
        RandomItem randomItem = RandomItem.getRPGItemHashMap().get(itemID);
        return randomItem.getItemStack(player);
    }

    public static void createItemStackConfig(ItemStack itemStack, String itemKey) {
        try {
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();

                Material material = itemStack.getType();

                String name = (itemMeta.hasDisplayName()) ? messageToOriginalText(itemMeta.getDisplayName()) : material.name();

                List<String> lores = new ArrayList<>();
                if (itemMeta.hasLore()) {
                    for (String lore : itemMeta.getLore()) {
                        lores.add(messageToOriginalText(lore));
                    }
                }

                short data = 0;
                if (Main.version > 1141) {
                    if (itemMeta.hasCustomModelData()) {
                        data = (short) itemMeta.getCustomModelData();
                    }
                } else {
                    data = (short) itemStack.getData().getData();
                }

                File file = new File(Main.getInstance().getDataFolder() + "/Items", itemKey + ".yml");
                if (!file.createNewFile()) {
                    return;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.createSection(itemKey);
                ConfigurationSection itemKeySection = config.getConfigurationSection(itemKey);
                itemKeySection.createSection("material");
                itemKeySection.createSection("data");
                itemKeySection.createSection("display");
                itemKeySection.createSection("lores");
                itemKeySection.createSection("nbt-keys");
                itemKeySection.createSection("enchants");
                itemKeySection.createSection("randoms");
                itemKeySection.createSection("computes");
                itemKeySection.set("material", material.toString());
                itemKeySection.set("data", data);
                itemKeySection.set("display", name);
                itemKeySection.set("lores", lores);
                NBTItem nbtItem = new NBTItem(itemStack);
                for (String key : nbtItem.getKeys()) {
                    if ((!"Damage".equals(key) && !"display".equals(key)) && !"Enchantments".equals(key)) {
                        if (nbtItem.getString(key) != null && !nbtItem.getString(key).isEmpty()) {
                            itemKeySection.getConfigurationSection("nbt-keys").set(key, "string:" + nbtItem.getString(key));
                        } else if (nbtItem.getDouble(key) != null) {
                            itemKeySection.getConfigurationSection("nbt-keys").set(key, "double:" + nbtItem.getDouble(key));
                        } else if (nbtItem.getInteger(key) != null) {
                            itemKeySection.getConfigurationSection("nbt-keys").set(key, "integer:" + nbtItem.getInteger(key));
                        }
                    }
                }
                if (itemMeta.hasEnchants()) {
                    for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                        if (enchantment != null) {
                            Integer level = itemMeta.getEnchantLevel(enchantment);
                            itemKeySection.getConfigurationSection("enchants").set(enchantment.getKey().getKey(), level);
                        }
                    }
                }
                config.save(file);
                Main.getInstance().loadConfig();
            }
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

                String material = objectSection.getString("material");

                String display = objectSection.getString(("display"));

                List<String> lores = objectSection.getStringList(("lores"));

                short data = (short) objectSection.getInt("data");

                ConfigurationSection nbtSection = objectSection.getConfigurationSection("nbt-keys");
                HashMap<String, String> nbtHashMap = new HashMap<>();
                if (nbtSection != null && !nbtSection.getKeys(false).isEmpty()) {
                    for (String nbtKey : nbtSection.getKeys(false)) {
                        nbtHashMap.put(nbtKey, nbtSection.getString(nbtKey));
                    }
                }

                ConcurrentHashMap<String, String> enchantsMap = new ConcurrentHashMap<>();
                ConfigurationSection enchantsSection = objectSection.getConfigurationSection("enchants");
                if (enchantsSection != null && !enchantsSection.getKeys(false).isEmpty()) {
                    for (String enchantKey : enchantsSection.getKeys(false)) {
                        enchantsMap.put(enchantKey, enchantsSection.getString(enchantKey));
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
                RandomItem randomItem = new RandomItem(key, display, material, lores, nbtHashMap, itemCompute, stringsMap, numbersMap, enchantsMap, data);
                RandomItem.getRPGItemHashMap().put(key, randomItem);
            }
        }
    }

    public short getData() {
        return data;
    }

    public ConcurrentHashMap<String, String> getEnchantsMap() {
        return enchantsMap;
    }

/*    public ItemStack getItemStack() {
        {
            ItemStack itemStack = new ItemStack(this.material, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(getMessage(this.display));
            List<String> newLores = new ArrayList<>();
            for (String lore : this.lores) {
                newLores.add(getMessage(lore));
            }
            itemMeta.setLore(newLores);
            for (String enchant : this.enchantsMap.keySet()) {
                int level = Integer.parseInt(enchantsMap.get(enchant));
                itemMeta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchant)), level, true);
            }
            itemStack.setItemMeta(itemMeta);
            NBTItem nbtItem = new NBTItem(itemStack);
            for (String key : this.nbtHashMap.keySet()) {
                if (!key.equals("Enchantments")) {
                    String value = this.nbtHashMap.get(key);
                    nbtItem.setString(key, value);
                }
            }

            return nbtItem.getItem();
        }
    }*/

    private String doReplace(String string, ConcurrentHashMap<String, String> computeMap, ConcurrentHashMap<String, String> numbersMap, ConcurrentHashMap<String, String> alreadyStringsMap, Player player) {
        for (String key : computeMap.keySet()) {
            String value = computeMap.get(key);
            string = string.replace("{" + key + "}", value);
        }
        {
            List<String> replaces = RandomItemUtils.extractMessageByTriangularBrackets(string);
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
                        string = string.replace("<" + replace + ">", RandomItemUtils.listToStringWithNext(RandomItemUtils.getStrings(replace, value)));
                        alreadyStringsMap.put(key, value);
                    }
                }
            }

        }
        for (String key : numbersMap.keySet()) {
            String value = numbersMap.get(key);
            string = string.replace("<" + key + ">", value);
        }
        return getMessage(PlaceholderAPI.setPlaceholders(player, string));
    }

    public ItemStack getItemStack(Player player) {
        ItemStack itemStack;
        if (Main.version > 1121) {
            itemStack = new ItemStack(Material.IRON_AXE, 1);
        } else {
            itemStack = new ItemStack(Material.IRON_AXE, 1, data);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        String display = new String((this.display == null) ? material : this.display);
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
        Material material = Material.matchMaterial(doReplace(this.material, computeMap, numbersMap, alreadyStringsMap, player));
        itemStack.setType(material);
        display = doReplace(display, computeMap, numbersMap, alreadyStringsMap, player);
        itemMeta.setDisplayName(display);
        if (Main.version >= 1141) {
            itemMeta.setCustomModelData(Integer.valueOf(data));
        }
        List<String> newLores = new ArrayList<>();
        List<String> loresClone = new ArrayList<>();
        for (String lore : lores) {
            loresClone.add(new String(lore));
        }
        for (String lore : loresClone) {
            lore = doReplace(lore, computeMap, numbersMap, alreadyStringsMap, player);
            if (lore.matches(".*<next>.*")) {
                Collections.addAll(newLores, lore.split("<next>"));
            } else {
                newLores.add(lore);
            }
        }
        itemMeta.setLore(newLores);
        for (String enchant : this.enchantsMap.keySet()) {
            String value = doReplace(this.enchantsMap.get(enchant), computeMap, numbersMap, alreadyStringsMap, player);
            int level = (int) Math.round(getResult(value));
            itemMeta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchant)), level, true);
        }
        itemStack.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(itemStack);
        for (String key : nbtHashMap.keySet()) {
            if (!key.equals("Enchantments")) {
                /*doReplace(, computeMap, alreadyStringsMap, player)*/
                String value = nbtHashMap.get(key);
                if (value.matches(".*string:.*")) {
                    value = doReplace(value.split(":")[1], computeMap, numbersMap, alreadyStringsMap, player);
                    nbtItem.setString(key, value);
                } else if (value.matches(".*double:.*")) {
                    value = doReplace(value.split(":")[1], computeMap, numbersMap, alreadyStringsMap, player);
                    nbtItem.setDouble(key, getResult(value));
                } else if (value.matches(".*integer:.*")) {
                    value = doReplace(value.split(":")[1], computeMap, numbersMap, alreadyStringsMap, player);
                    nbtItem.setInteger(key, (int) Math.round(getResult(value)));
                }
            }
        }

        return nbtItem.getItem();
    }

    public String getId() {
        return this.id;
    }

    public String getDisplay() {
        return this.display;
    }

    public String getMaterial() {
        return this.material;
    }

    public List<String> getLores() {
        return this.lores;
    }


    public ConcurrentHashMap<String, List<String>> getStringsMap() {
        return stringsMap;
    }

    public ConcurrentHashMap<String, String> getNumbersMap() {
        return numbersMap;
    }

    public HashMap<String, String> getNBTHashMap() {
        return this.nbtHashMap;
    }

    public ItemCompute getItemCompute() {
        return itemCompute;
    }
}
