package com.skillw.randomitem.api.section.weight;

import com.skillw.randomitem.api.data.BasicData;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.section.weight.Weighable
 * @date 2021-02-09 16:37:34
 * Copyright  2020 user. All rights reserved.
 * @apiNote : The class which impl this must be instance of BaseSection!
 * @apiNote : And the map in the class must have a key "values" and its value List<BasicData<?>>
 */
public interface Weighable<T extends BasicData<?>> {
    /**
     * To get the List of BasicData
     *
     * @return the List of BasicData
     */
    List<T> getObjects();

    /**
     * To get the result of weight random
     *
     * @return the result of weight random
     */
    T getWeightRandom(Player player);

}
