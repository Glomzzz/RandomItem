package com.skillw.randomitem.api.section.weight;

import com.skillw.randomitem.api.data.BaseData;
import com.skillw.randomitem.util.ProcessUtils;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.section.weight.Weighable
 * @date 2021-02-09 16:37:34
 * Copyright  2020 user. All rights reserved.
 * The class which impl this must extend BaseSection!
 * And the map in the BaseSection must have a key "value-map" and its value ConcurrentHashMap<String and BaseData>
 */
public interface Weighable<T extends BaseData<?>> {
    /**
     * To get the result of random base on weights
     *
     * @param weighable the weighable
     * @param player    the player
     * @return the result of random base on weights
     */
    static BaseData<?> getWeightRandom(Weighable<? extends BaseData<?>> weighable, Player player) {
        ConcurrentHashMap<String, BaseData<?>> valueMap = new ConcurrentHashMap<>();
        for (BaseData<?> baseData : weighable.getValueMapClone().values()) {
            valueMap.put(baseData.getId(), baseData.clone());
        }
        return ProcessUtils.getRandomBasicDataBaseOnWeight(valueMap, player);
    }

    /**
     * To get the clone of Map of String and T
     *
     * @return the clone of Map of String and T
     */
    ConcurrentHashMap<String, T> getValueMapClone();
}
