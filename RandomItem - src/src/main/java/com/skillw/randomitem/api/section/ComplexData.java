package com.skillw.randomitem.api.section;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.section.ComplexData
 * @date 2021-02-11 11:22:37
 * Copyright  2020 user. All rights reserved.
 */
public interface ComplexData {
    /**
     * To get the BaseSection map
     *
     * @return the BaseSection map (BaseSection's id,BaseSection)
     */
    ConcurrentHashMap<String, BaseSection> getSectionMap();

    /**
     * To set the BaseSection map (BaseSection's id,BaseSection)
     *
     * @param sectionMap the BaseSection map (BaseSection's id,BaseSection)
     */
    void setSectionMap(ConcurrentHashMap<String, BaseSection> sectionMap);

    /**
     * To get the map of the value of the loaded BaseSection
     *
     * @return the map of the value of the loaded BaseSection
     */
    ConcurrentHashMap<String, LinkedList<String>> getAlreadySectionMap();

    /**
     * To set the map of the value of the loaded BaseSection
     *
     * @param alreadySectionMap the map of the value of the loaded BaseSection
     */
    void setAlreadySectionMap(ConcurrentHashMap<String, LinkedList<String>> alreadySectionMap);

    /**
     * To get the player
     *
     * @return the player
     */
    Player getPlayer();

    /**
     * To set the player
     *
     * @param player the player
     */
    void setPlayer(Player player);
}
