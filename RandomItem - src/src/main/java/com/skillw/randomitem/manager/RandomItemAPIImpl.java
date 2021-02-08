package com.skillw.randomitem.manager;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.RandomItemAPI;
import com.skillw.randomitem.api.object.*;
import com.skillw.randomitem.compute.ItemComputeImpl;
import com.skillw.randomitem.item.RandomItemImpl;
import com.skillw.randomitem.string.SubStringImpl;
import com.skillw.randomitem.utils.ConfigUtils;
import com.skillw.randomitem.utils.RandomItemUtils;
import com.skillw.randomitem.weight.PairImpl;
import com.skillw.randomitem.weight.WeightRandomImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.RandomItemUtils.addGlobalRandom;
import static com.skillw.randomitem.utils.RandomItemUtils.loadRandomsFromSection;

/**
 * @ClassName : com.skillw.randomitem.api.RandomItemAPI
 * Created by Glom_ on 2021-02-04 17:34:37
 * Copyright  2020 user. All rights reserved.
 */
public class RandomItemAPIImpl implements RandomItemAPI {
    @Override
    public ItemCompute createItemCompute() {
        return new ItemComputeImpl();
    }

    @Override
    public Pair createPair(Object key, Object value) {
        return new PairImpl(key, value);
    }

    @Override
    public RandomItem createRandomItem(ConfigurationSection section) {
        String key = section.getName();

        String material = section.getString("material");

        String display = section.getString(("display"));

        List<String> lores = section.getStringList(("lores"));

        short data = (short) section.getInt("data");

        ConfigurationSection nbtSection = section.getConfigurationSection("nbt-keys");

        ConcurrentHashMap<String, String> enchantsMap = new ConcurrentHashMap<>();
        ConfigurationSection enchantsSection = section.getConfigurationSection("enchants");
        if (enchantsSection != null && !enchantsSection.getKeys(false).isEmpty()) {
            for (String enchantKey : enchantsSection.getKeys(false)) {
                enchantsMap.put(enchantKey, enchantsSection.getString(enchantKey));
            }
        }

        ItemCompute itemCompute = new ItemComputeImpl();
        ConcurrentHashMap<String, List<SubString>> stringsMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> numbersMap = new ConcurrentHashMap<>(ConfigUtils.getGlobalNumbersMap());
        {
            ConfigurationSection randomsSection = section.getConfigurationSection("randoms");
            loadRandomsFromSection(stringsMap, numbersMap, itemCompute, randomsSection);
            addGlobalRandom(stringsMap, numbersMap, itemCompute);
        }
        itemCompute.setNumberMap(numbersMap);
        return this.createRandomItem(key, display, material, data, lores, nbtSection, itemCompute, stringsMap, numbersMap, enchantsMap);
    }

    @Override
    public RandomItem createRandomItem(String id, String display, String material, short data, List<String> lores, ConfigurationSection nbtSection, ItemCompute itemCompute, ConcurrentHashMap<String, List<SubString>> stringsMap, ConcurrentHashMap<String, String> numbersMap, ConcurrentHashMap<String, String> enchantsMap) {
        return new RandomItemImpl(id, display, material, data, lores, nbtSection, itemCompute, stringsMap, numbersMap, enchantsMap);
    }

    @Override
    public SubString createSubString(String id, double weight, List<String> strings) {
        return new SubStringImpl(id, weight, strings);
    }

    @Override
    public WeightRandom createWeightRandom(List<Pair<?, ? extends Number>> pairs) {
        return new WeightRandomImpl(pairs);
    }

    @Override
    public void reload() {
        Main.getInstance().loadConfig();
    }

    @Override
    public void reloadRandomItems() {
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
                RandomItem randomItem = this.createRandomItem(objectSection);
                randomItem.register();
            }
        }
    }
}
