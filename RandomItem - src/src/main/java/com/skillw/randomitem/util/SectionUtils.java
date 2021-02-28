package com.skillw.randomitem.util;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.util.DebugUtils.debugSection;

/**
 * @ClassName : com.skillw.randomitem.utils.SectionHandler
 * Created by Glom_ on 2021-02-15 17:38:06
 * Copyright  2020 user. All rights reserved.
 */
public final class SectionUtils {
    private SectionUtils() {
    }

    public static void addGlobalRandomSections(ConcurrentHashMap<String, BaseSection> sectionMap, List<String> usedGlobalSection) {
        sendDebug("&d - &aAdding Global Sections:");
        ConcurrentHashMap<String, BaseSection> globalSectionMap = ConfigUtils.getGlobalSectionMapClone();
        for (String key : globalSectionMap.keySet()) {
            if (sectionMap.containsKey(key)) {
                continue;
            }
            if (usedGlobalSection == null || usedGlobalSection.isEmpty()) {
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

    public static void addRandomSectionsFromConfigSection(ConcurrentHashMap<String, BaseSection> baseSectionMap, ConfigurationSection randomsSection) {
        baseSectionMap.putAll(getRandomSectionsFromSection(randomsSection));
    }

    public static ConcurrentHashMap<String, BaseSection> getRandomSectionsFromSection(ConfigurationSection randomsSection) {
        ConcurrentHashMap<String, BaseSection> baseSectionMap = new ConcurrentHashMap<>();
        if (randomsSection != null) {
            for (String randomKey : randomsSection.getKeys(false)) {
                Object object = randomsSection.get(randomKey);
                if (object instanceof ConfigurationSection) {
                    ConfigurationSection randomSection = (ConfigurationSection) object;
                    String type = randomSection.getString("type");
                    if (type == null) {
                        continue;
                    }
                    if (!(randomSection.get("fixed") instanceof ConfigurationSection)) {
                        String fixed = randomSection.getString("fixed");
                        randomSection.set("fixed", null);
                        randomSection.set("fixed.decimal.min", fixed);
                    }
                    for (BaseSectionType sectionType : BaseSectionType.getSectionTypes()) {
                        sectionType.loadIfSameType(type, randomSection, baseSectionMap);
                    }
                } else {
                    String simple = randomsSection.getString(randomKey);
                    if (simple == null || simple.isEmpty()) {
                        continue;
                    }
                    for (BaseSectionType sectionType : BaseSectionType.getSectionTypes()) {
                        sectionType.loadIfSameType(simple, null, baseSectionMap);
                    }
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
