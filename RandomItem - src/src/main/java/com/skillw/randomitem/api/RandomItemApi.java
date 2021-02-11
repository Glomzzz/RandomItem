package com.skillw.randomitem.api;

import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.weightrandom.WeightRandom;
import com.skillw.randomitem.string.SubString;
import io.izzel.taboolib.util.Pair;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.RandomItemAPI
 * @date 2021-02-04 18:01:15
 * Copyright  2020 user. All rights reserved.
 */
public interface RandomItemApi {

    /**
     * To create a new Pair with a key and a value.
     *
     * @param key   the key of the Pair
     * @param value the value of the Pair
     * @return
     */
    Pair<?, ?> createPair(Object key, Object value);

    /**
     * To create a new RandomItem by a ConfigurationSection
     *
     * @param section the ConfigurationSection.
     * @return A new RandomItem about the ConfigurationSection
     */
    RandomItem createRandomItem(ConfigurationSection section);

    /**
     * To create a new RandomItem with some params.
     *
     * @param id             the new id you want
     * @param display        the display
     * @param material       the material
     * @param data           the data
     * @param lores          the lores
     * @param nbtSection     the NBT ConfigurationSection
     * @param enchantmentMap the Enchantment Map (Enchantment's name,Level formula)
     * @param sectionMap     the BaseSection Map (BaseSection's id,BaseSection)
     * @return A new RandomItem
     */
    RandomItem createRandomItem(String id, String display, String material, String data, List<String> lores, ConfigurationSection nbtSection, ConcurrentHashMap<String, String> enchantmentMap, ConcurrentHashMap<String, BaseSection> sectionMap);

    /**
     * To create a new SubString.
     *
     * @param id      the new id you want
     * @param weight  the weight
     * @param strings the strings
     * @return A new SubString
     */
    SubString createSubString(String id, double weight, List<String> strings);

    /**
     * To create a new WeightRandom.
     *
     * @param pairs the List of Pair<Object, Weight>
     * @return A new WeightRandom
     */
    WeightRandom<?, ? extends Number> createWeightRandom(List<Pair<?, ? extends Number>> pairs);

    /**
     * To reload the all config of the plugin
     */
    void reload();

    /**
     * To reload the RandomItems
     */
    void reloadRandomItems();
}
