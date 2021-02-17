package com.skillw.randomitem.utils;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.sendDebug;

/**
 * @ClassName : com.skillw.randomitem.utils.SectionHandler
 * Created by Glom_ on 2021-02-15 17:38:06
 * Copyright  2020 user. All rights reserved.
 */
public final class SectionUtils {
    private SectionUtils() {
    }

    public static void debugSection(BaseSection baseSection) {
        if (baseSection instanceof Debuggable) {
            sendDebug("&d -> &b" + baseSection.getId() + " &5: ");
            for (String string : ((Debuggable) baseSection).getDebugMessages()) {
                sendDebug("&d     " + string);
            }
        } else {
            sendDebug("&d -> &b" + baseSection.getId());
        }
    }

    public static void addGlobalRandomSections(ConcurrentHashMap<String, BaseSection> sectionMap, List<String> usedGlobalSection) {
        sendDebug("&d - &aAdding Global Sections:");
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


    public static void addRandomsFromSection(ConcurrentHashMap<String, BaseSection> baseSectionMap, ConfigurationSection randomsSection) {
        baseSectionMap.putAll(loadRandomsFromSection(randomsSection));
    }

    public static ConcurrentHashMap<String, BaseSection> loadRandomsFromSection(ConfigurationSection randomsSection) {
        ConcurrentHashMap<String, BaseSection> baseSectionMap = new ConcurrentHashMap<>();
        for (String randomKey : randomsSection.getKeys(false)) {
            ConfigurationSection randomSection = randomsSection.getConfigurationSection(randomKey);
            String type = null;
            if (randomSection != null) {
                type = randomSection.getString("type");
            }
            if (type == null && (type = randomsSection.getString(randomKey)) == null) {
                continue;
            }
            for (BaseSectionType sectionType : BaseSectionType.getSectionTypes()) {
                sectionType.loadIfSameType(type, randomSection, baseSectionMap);
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
