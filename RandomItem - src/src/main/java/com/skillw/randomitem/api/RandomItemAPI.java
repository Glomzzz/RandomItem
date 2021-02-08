package com.skillw.randomitem.api;

import com.skillw.randomitem.api.object.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.api.RandomItemAPI
 * Created by Glom_ on 2021-02-04 18:01:15
 * Copyright  2020 user. All rights reserved.
 */
public interface RandomItemAPI {
    ItemCompute createItemCompute();

    Pair createPair(Object key, Object value);

    RandomItem createRandomItem(ConfigurationSection section);

    RandomItem createRandomItem(String id, String display, String material, short data, List<String> lores, ConfigurationSection nbtSection, ItemCompute itemCompute, ConcurrentHashMap<String, List<SubString>> stringsMap, ConcurrentHashMap<String, String> numbersMap, ConcurrentHashMap<String, String> enchantsMap);

    SubString createSubString(String id, double weight, List<String> strings);

    WeightRandom createWeightRandom(List<Pair<?, ? extends Number>> pairs);

    void reload();

    void reloadRandomItems();
}
