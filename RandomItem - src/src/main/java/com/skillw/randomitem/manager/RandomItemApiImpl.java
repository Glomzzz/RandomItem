package com.skillw.randomitem.manager;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.RandomItemApi;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.weightrandom.WeightRandom;
import com.skillw.randomitem.item.RandomItemImpl;
import com.skillw.randomitem.string.SubString;
import com.skillw.randomitem.utils.RandomItemUtils;
import com.skillw.randomitem.weight.WeightRandomImpl;
import io.izzel.taboolib.util.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.isDebug;
import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.utils.RandomItemUtils.*;

/**
 * @ClassName : com.skillw.randomitem.manager.RandomItemApiImpl
 * Created by Glom_ on 2021-02-04 17:34:37
 * Copyright  2020 user. All rights reserved.
 */
public class RandomItemApiImpl implements RandomItemApi {

    @Override
    public Pair<?, ?> createPair(Object key, Object value) {
        return Pair.of(key, value);
    }

    @Override
    public RandomItem createRandomItem(ConfigurationSection section) {
        String key = section.getName();
        sendDebug("&aLoading item: &6" + key);
        String display = section.getString(("display"));
        sendDebug("&d- &aDisplay: &6" + display);
        String material = section.getString("material");
        sendDebug("&d- &aMaterial: &6" + material);
        String data = section.getString("data");
        sendDebug("&d- &aData: &6" + data);
        List<String> lores = section.getStringList(("lores"));
        if (!lores.isEmpty()) {
            sendDebug("&d- &aLores: ");
            for (String lore : lores) {
                sendDebug("&d  - &f" + lore);
            }
        }
        ConfigurationSection nbtSection = section.getConfigurationSection("nbt-keys");
        if (isDebug()) {
            if (!nbtSection.getKeys(false).isEmpty()) {
                sendDebug("&d- &aNBT-keys: ");
                for (String nbtKey : nbtSection.getKeys(false)) {
                    sendDebug("&d  -> &b" + nbtKey + " &5= &e" + nbtSection.get(nbtKey));
                }
            }
        }
        ConcurrentHashMap<String, String> enchantmentMap = new ConcurrentHashMap<>();
        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null && !enchantmentsSection.getKeys(false).isEmpty()) {
            sendDebug("&d- &aEnchantments: ");
            for (String enchantmentKey : enchantmentsSection.getKeys(false)) {
                sendDebug("&d  -> &b" + enchantmentKey + " &5= &e" + enchantmentsSection.getString(enchantmentKey));
                enchantmentMap.put(enchantmentKey, enchantmentsSection.getString(enchantmentKey));
            }
        }
        ConcurrentHashMap<String, BaseSection> sectionMap = new ConcurrentHashMap<>();
        sendDebug("&d- &aRandom sections: ");
        {
            ConfigurationSection randomsSection = section.getConfigurationSection("randoms");
            addRandomsFromSection(sectionMap, randomsSection);
            if (!sectionMap.isEmpty()) {
                sendDebug("&d- &aIndividual sections:");
                for (String sectionKey : sectionMap.keySet()) {
                    BaseSection baseSection = sectionMap.get(sectionKey);
                    debugSection(baseSection);
                }
            }
            addGlobalRandom(sectionMap);
        }
        return this.createRandomItem(key, display, material, data, lores, nbtSection, enchantmentMap, sectionMap);
    }

    @Override
    public RandomItem createRandomItem(String id, String display, String material, String data, List<String> lores, ConfigurationSection nbtSection, ConcurrentHashMap<String, String> enchantmentMap, ConcurrentHashMap<String, BaseSection> sectionMap) {
        return new RandomItemImpl(id, display, material, data, lores, nbtSection, enchantmentMap, sectionMap);
    }

    @Override
    public SubString createSubString(String id, double weight, List<String> strings) {
        return new SubString(id, weight, strings);
    }

    @Override
    public WeightRandom<?, ? extends Number> createWeightRandom(List<Pair<?, ? extends Number>> pairs) {
        return new WeightRandomImpl(pairs);
    }

    @Override
    public void reload() {
        Main.getInstance().loadConfig();
    }

    @Override
    public void reloadRandomItems() {
        sendDebug("&aLoading items:");
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
