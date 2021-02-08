package com.skillw.randomitem.api.object;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.compute.ItemComputeI
 * Created by Glom_ on 2021-02-04 17:17:21
 * Copyright  2020 user. All rights reserved.
 */
public interface ItemCompute {
    void addComputeFromSection(ConfigurationSection section);

    ConcurrentHashMap<String, String> getMaxMap();

    void setMaxMap(ConcurrentHashMap<String, String> maxMap);

    ConcurrentHashMap<String, String> getFixedMap();

    void setFixedMap(ConcurrentHashMap<String, String> fixedMap);

    ConcurrentHashMap<String, String> getMaxMapClone();

    ConcurrentHashMap<String, String> getComputeMap();

    void setComputeMap(ConcurrentHashMap<String, String> computeMap);

    ConcurrentHashMap<String, String> getNumberMap();

    void setNumberMap(ConcurrentHashMap<String, String> numberMap);

    ConcurrentHashMap<String, List<SubString>> getSubStringMap();

    void setSubStringMap(ConcurrentHashMap<String, List<SubString>> subStringMap);

    ConcurrentHashMap<String, String> getComputeMapClone();

    ConcurrentHashMap<String, String> getFixedMapClone();

    ItemCompute clone();

    ConcurrentHashMap<String, String> calculateCompute(UUID uuid);

    ConcurrentHashMap<String, List<String>> getAlreadyStringMap();

    void setAlreadyStringMap(ConcurrentHashMap<String, List<String>> alreadyStringMap);
}
