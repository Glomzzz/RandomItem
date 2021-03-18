package com.skillw.randomitem.util;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.util.DebugUtils.debugSection;
import static com.skillw.randomitem.util.DebugUtils.sendDebug;

/**
 * @ClassName : com.skillw.randomitem.utils.SectionHandler
 * Created by Glom_ on 2021-02-15 17:38:06
 * Copyright  2020 user. All rights reserved.
 */
public final class SectionUtils {
    private SectionUtils() {
    }

    public static void addGlobalRandomSections(ConcurrentHashMap<String, BaseSection> sectionMap, List<String> usedGlobalSection, boolean isDebug) {
        sendDebug("&d - &aAdding Global Sections:", isDebug);
        ConcurrentHashMap<String, BaseSection> globalSectionMap = ConfigUtils.getGlobalSectionMapClone();
        for (String key : globalSectionMap.keySet()) {
            if (sectionMap.containsKey(key)) {
                continue;
            }
            if (usedGlobalSection == null) {
                BaseSection baseSection = globalSectionMap.get(key);
                debugSection(baseSection);
                sectionMap.put(key, baseSection);
            } else {
                for (String neededKey : usedGlobalSection) {
                    if (key.equals(neededKey)) {
                        BaseSection baseSection = globalSectionMap.get(key);
                        debugSection(baseSection);
                        sectionMap.put(key, baseSection);
                    }
                }
            }

        }
    }

    public static void addRandomSectionsFromConfigSection(ConcurrentHashMap<String, BaseSection> baseSectionMap, ConfigurationSection randomsSection, boolean isDebug) {
        baseSectionMap.putAll(getRandomSectionsFromSection(randomsSection, isDebug));
    }

    public static ConcurrentHashMap<String, BaseSection> getRandomSectionsFromSection(ConfigurationSection randomsSection, boolean isDebug) {
        ConcurrentHashMap<String, BaseSection> baseSectionMap = new ConcurrentHashMap<>();
        if (randomsSection != null) {
            for (String randomKey : randomsSection.getKeys(false)) {
                Object object = randomsSection.get(randomKey);
                BaseSection baseSection;
                if (object instanceof ConfigurationSection) {
                    ConfigurationSection randomSection = (ConfigurationSection) object;
                    String type = randomSection.getString("type");
                    if (type == null) {
                        continue;
                    }
                    //旧配置升级为新配置
                    if (!(randomSection.get("fixed") instanceof ConfigurationSection)) {
                        String fixed = randomSection.getString("fixed");
                        randomSection.set("fixed", null);
                        randomSection.set("fixed.decimal.min", fixed);
                    }
                    baseSection = BaseSectionType.load(type, randomSection, isDebug);
                } else {
                    String simple = randomsSection.getString(randomKey);
                    if (simple == null || simple.isEmpty()) {
                        continue;
                    }
                    baseSection = BaseSectionType.load(simple, null, isDebug);
                }
                if (baseSection != null) {
                    baseSectionMap.put(baseSection.getId(), baseSection);
                }
            }
        }
        return baseSectionMap;
    }

    public static ConcurrentHashMap<String, BaseSection> cloneBaseSectionMap(ConcurrentHashMap<String, BaseSection> map) {
        ConcurrentHashMap<String, BaseSection> newMap = new ConcurrentHashMap<>();
        for (String key : map.keySet()) {
            BaseSection section = map.get(key);
            newMap.put(key, section.clone());
        }
        return newMap;
    }
}
