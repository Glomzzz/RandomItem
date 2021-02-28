package com.skillw.randomitem.bean;

import com.skillw.randomitem.api.randomitem.ItemData;
import com.skillw.randomitem.api.section.BaseSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.util.SectionUtils.cloneBaseSectionMap;

/**
 * @ClassName : com.skillw.randomitem.api.randomitem.ItemData
 * Created by Glom_ on 2021-02-14 18:01:45
 * Copyright  2020 user. All rights reserved.
 */
public class ItemDataImpl implements ItemData {
    private final String id;
    private final String display;
    private final String material;
    private final String data;
    private final List<String> lores;
    private final List<String> usedGlobalSections;
    private final String unbreakable;
    private final List<String> itemFlags;
    private final ConcurrentHashMap<String, String> enchantMap;
    private final ConcurrentHashMap<String, BaseSection> sectionMap;
    private final ConfigurationSection itemSection;

    public ItemDataImpl(String id,
                        String display,
                        String material,
                        String data,
                        List<String> lores,
                        List<String> usedGlobalSections,
                        String unbreakable,
                        List<String> itemFlags,
                        ConcurrentHashMap<String, String> enchantMap,
                        ConcurrentHashMap<String, BaseSection> sectionMap,
                        ConfigurationSection itemSection) {
        this.id = id;
        this.display = display;
        this.material = material;
        this.data = data;
        this.lores = lores;
        this.usedGlobalSections = usedGlobalSections;
        this.unbreakable = unbreakable;
        this.itemFlags = itemFlags;
        this.enchantMap = enchantMap;
        this.sectionMap = sectionMap;
        this.itemSection = itemSection;
    }

    @Override
    public String getUnbreakableFormula() {
        return this.unbreakable == null ? "false" : this.unbreakable;
    }

    @Override
    public List<String> getItemFlagsClone() {
        if (this.itemFlags == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(this.itemFlags);
        }
    }

    @Override
    public ConfigurationSection getItemSection() {
        return this.itemSection;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getDisplay() {
        return this.display == null ? this.getMaterial() : this.display;
    }

    @Override
    public String getMaterial() {
        return this.material == null ? "STONE" : this.material;
    }

    @Override
    public String getData() {
        return this.data == null ? "0" : this.data;
    }

    @Override
    public List<String> getLoresClone() {
        if (this.lores == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(this.lores);
        }
    }

    @Override
    public List<String> getUsedGlobalSectionsClone() {
        if (this.usedGlobalSections != null) {
            return new ArrayList<>(this.usedGlobalSections);
        }
        return new ArrayList<>();
    }

    @Override
    public ConcurrentHashMap<String, String> getEnchantMapClone() {
        if (this.enchantMap != null) {
            return new ConcurrentHashMap<>(this.enchantMap);
        }
        return new ConcurrentHashMap<>();

    }

    @Override
    public ConcurrentHashMap<String, BaseSection> getSectionMapClone() {
        if (this.sectionMap != null) {
            return cloneBaseSectionMap(this.sectionMap);
        }
        return new ConcurrentHashMap<>();
    }
}
