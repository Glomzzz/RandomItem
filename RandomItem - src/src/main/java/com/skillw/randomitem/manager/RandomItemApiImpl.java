package com.skillw.randomitem.manager;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.RandomItemApi;
import com.skillw.randomitem.api.randomitem.ItemData;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.bean.ItemDataImpl;
import com.skillw.randomitem.item.RandomItemImpl;
import com.skillw.randomitem.string.SubString;
import com.skillw.randomitem.util.ConfigUtils;
import com.skillw.randomitem.util.ProcessUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.isDebug;
import static com.skillw.randomitem.util.DebugUtils.*;
import static com.skillw.randomitem.util.SectionUtils.addGlobalRandomSections;
import static com.skillw.randomitem.util.SectionUtils.addRandomSectionsFromConfigSection;

/**
 * @ClassName : com.skillw.randomitem.manager.RandomItemApiImpl
 * Created by Glom_ on 2021-02-04 17:34:37
 * Copyright  2020 user. All rights reserved.
 */
public final class RandomItemApiImpl implements RandomItemApi {

    @Override
    public List<String> replaceAll(List<String> strings, ComplexData data) {
        return ProcessUtils.replaceAll(strings, data);
    }

    @Override
    public String replaceAll(String string, ComplexData data) {
        return ProcessUtils.replaceAll(string, data);
    }

    @Override
    public void handlePointData(String pointData, ComplexData data) {
        ProcessUtils.handlePointData(pointData, data);
    }

    @Override
    public RandomItem createRandomItem(ConfigurationSection section, boolean isDebug) {
        return this.createRandomItem(this.createItemData(section, isDebug));
    }

    @Override
    public RandomItem createRandomItem(ItemData itemData) {
        return new RandomItemImpl(itemData);
    }

    @Override
    public ItemData createItemData(ConfigurationSection section, boolean isDebug) {
        String key = section.getName();
        sendDebug("&aLoading item: &6" + key, isDebug);

        String material = section.getString("material") == null ? "Nope" : section.getString("material");
        String display = section.getString("display") == null ? material : section.getString("display");
        sendDebug("&d- &aDisplay: &6" + display, isDebug);
        sendDebug("&d- &aMaterial: &6" + material, isDebug);

        String data = section.getString("data") == null ? "0" : section.getString("data");
        sendDebug("&d- &aData: &6" + data, isDebug);

        List<String> lores = section.getStringList("lores") == null ? new ArrayList<>() : section.getStringList("lores");
        if (!lores.isEmpty()) {
            sendDebug("&d- &aLores: ", isDebug);
            for (String lore : lores) {
                sendDebug("&d  - &f" + lore, isDebug);
            }
        }

        String unbreakable = section.getString("unbreakable") == null ? "false" : section.getString("unbreakable");
        sendDebug("&d- &aUnbreakable: &6" + unbreakable, isDebug);

        List<String> flags = section.getStringList("item-flags") == null ? new ArrayList<>() : section.getStringList("item-flags");
        if (!flags.isEmpty()) {
            sendDebug("&d- &aItemFlags: ", isDebug);
            for (String flag : flags) {
                sendDebug("&d  - &f" + flag, isDebug);
            }
        }

        ConfigurationSection nbtSection = section.getConfigurationSection("nbt-keys");
        if (isDebug()) {
            if (!nbtSection.getKeys(false).isEmpty()) {
                sendDebug("&d- &aNBT-keys: ", isDebug);
                debugConfigurationSection(nbtSection, null);
            }
        }
        ConcurrentHashMap<String, String> enchantmentMap = new ConcurrentHashMap<>();
        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null && !enchantmentsSection.getKeys(false).isEmpty()) {
            sendDebug("&d- &aEnchantments: ", isDebug);
            for (String enchantmentKey : enchantmentsSection.getKeys(false)) {
                sendDebug("&d  -> &b" + enchantmentKey + " &5= &e" + enchantmentsSection.getString(enchantmentKey), isDebug);
                enchantmentMap.put(enchantmentKey, enchantmentsSection.getString(enchantmentKey));
            }
        }
        ConcurrentHashMap<String, BaseSection> sectionMap = new ConcurrentHashMap<>();
        List<String> usedGlobalSection = section.getStringList("used-global-sections");

        sendDebug("&d- &aRandom sections: ", isDebug);
        ConfigurationSection randomsSection = section.getConfigurationSection("randoms");
        addRandomSectionsFromConfigSection(sectionMap, randomsSection, isDebug);
        if (!sectionMap.isEmpty()) {
            sendDebug("&d- &aIndividual Random Sections:", isDebug);
            for (String sectionKey : sectionMap.keySet()) {
                BaseSection baseSection = sectionMap.get(sectionKey);
                debugSection(baseSection);
            }
        }
        addGlobalRandomSections(sectionMap, usedGlobalSection, isDebug);

        return this.createItemData(key, display, material, data, lores, usedGlobalSection, unbreakable, flags, enchantmentMap, sectionMap, section);
    }

    @Override
    public ItemData createItemData(String id,
                                   String display,
                                   String material,
                                   String data,
                                   List<String> lores,
                                   List<String> usedGlobalSections,
                                   String unbreakable,
                                   List<String> itemFlags,
                                   ConcurrentHashMap<String, String> enchantmentMap,
                                   ConcurrentHashMap<String, BaseSection> sectionMap,
                                   ConfigurationSection itemSection) {
        return new ItemDataImpl(id, display, material, data, lores, usedGlobalSections, unbreakable, itemFlags, enchantmentMap, sectionMap, itemSection);
    }

    @Override
    public SubString createSubString(String id, String weight, List<String> strings) {
        return new SubString(id, weight, strings);
    }

    @Override
    public void reload() {
        Main.getInstance().loadConfig();
    }

    @Override
    public void reloadRandomItems() {
        ConfigUtils.loadRandomItems();
    }
}
