package com.skillw.randomitem.api.randomitem;

import com.skillw.randomitem.api.section.BaseSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.api.randomitem.ItemData
 * Created by Glom_ on 2021-02-14 18:05:37
 * Copyright  2020 user. All rights reserved.
 */
public interface ItemData {
    /**
     * To get the unbreakable formula (0 = false , not 0 = true)
     *
     * @return the unbreakable formula
     */
    String getUnbreakableFormula();

    /**
     * To get the ItemFlag's id List's clone
     *
     * @return the ItemFlag's id List's clone
     */
    List<String> getItemFlagsClone();

    /**
     * To get the ConfigurationSection of this RandomItem
     *
     * @return the ConfigurationSection of this RandomItem
     */
    ConfigurationSection getItemSection();

    /**
     * To get the id
     *
     * @return the id
     */
    String getId();

    /**
     * To get the display
     *
     * @return the display
     */
    String getDisplay();

    /**
     * To get the material
     *
     * @return the material
     */
    String getMaterial();

    /**
     * To get the lores
     *
     * @return the lores
     */
    List<String> getLoresClone();

    /**
     * To get the data of the ItemStack
     *
     * @return the data of the ItemStack (1.14+ CustomModelData)
     */
    String getData();

    /**
     * To get the Enchantment Map (Enchantment's name,Level formula)
     *
     * @return The Enchantment Map (Enchantment's name,Level formula)
     */
    ConcurrentHashMap<String, String> getEnchantMapClone();

    /**
     * To get the BaseSection Map (BaseSection's id,BaseSection)
     *
     * @return the BaseSection Map (BaseSection's id,BaseSection)
     */
    ConcurrentHashMap<String, BaseSection> getSectionMapClone();

    /**
     * To get used global sections
     *
     * @return used global sections
     */
    List<String> getUsedGlobalSectionsClone();
}
